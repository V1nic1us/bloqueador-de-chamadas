package br.com.V1nic1us.callblocker

import android.telecom.Call
import android.telecom.CallScreeningService as AndroidCallScreeningService

class CallScreeningService : AndroidCallScreeningService() {
    override fun onScreenCall(callDetails: Call.Details) {
        val response = AndroidCallScreeningService.CallResponse.Builder()
        val handle = callDetails.handle
        val preferences = CallFilterPreferences(this)
        when (
            CallScreeningPolicy.evaluate(
                isIncomingCall = callDetails.callDirection == Call.Details.DIRECTION_INCOMING,
                handleScheme = handle?.scheme,
                address = handle?.schemeSpecificPart,
                filterEnabled = preferences.isEnabled(),
                mode = preferences.mode(),
            )
        ) {
            CallAction.ALLOW -> Unit
            CallAction.REJECT -> response.setDisallowCall(true).setRejectCall(true)
            CallAction.SILENCE -> response.setSilenceCall(true)
        }

        respondToCall(callDetails, response.build())
    }
}
