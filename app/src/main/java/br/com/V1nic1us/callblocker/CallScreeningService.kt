package br.com.V1nic1us.callblocker

import android.telecom.Call
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.ScheduledThreadPoolExecutor
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import android.telecom.CallScreeningService as AndroidCallScreeningService

class CallScreeningService : AndroidCallScreeningService() {
    override fun onScreenCall(callDetails: Call.Details) {
        val handle = callDetails.handle
        val preferences = CallFilterPreferences(this)
        val filterEnabled = preferences.isEnabled()
        val mode = preferences.mode()
        val isIncomingCall = callDetails.callDirection == Call.Details.DIRECTION_INCOMING
        val address = handle?.schemeSpecificPart
        val respond: (Boolean) -> Unit = { isKnownContact ->
            val response = AndroidCallScreeningService.CallResponse.Builder()
            when (
                CallScreeningPolicy.evaluate(
                    isIncomingCall = isIncomingCall,
                    handleScheme = handle?.scheme,
                    address = address,
                    filterEnabled = filterEnabled,
                    mode = mode,
                    isKnownContact = isKnownContact,
                )
            ) {
                CallAction.ALLOW -> Unit
                CallAction.REJECT -> response.setDisallowCall(true).setRejectCall(true)
                CallAction.SILENCE -> response.setSilenceCall(true)
            }
            respondToCall(callDetails, response.build())
        }

        if (filterEnabled && isIncomingCall && handle?.scheme == "tel" && !address.isNullOrBlank()) {
            timedLookup.start(
                lookup = { ContactLookup(applicationContext).contains(address) },
                complete = respond,
            )
        } else {
            respond(false)
        }
    }

    private companion object {
        // Telecom ignores responses after five seconds. Reserve time for the response IPC.
        val timedLookup = TimedContactLookup(
            worker = ThreadPoolExecutor(2, 2, 0, TimeUnit.MILLISECONDS, ArrayBlockingQueue(8)),
            scheduler = ScheduledThreadPoolExecutor(2),
            timeoutMillis = 3_500,
        )
    }
}
