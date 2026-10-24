package br.com.V1nic1us.callblocker

import android.telecom.Call
import android.telecom.CallScreeningService as AndroidCallScreeningService

class CallScreeningService : AndroidCallScreeningService() {
    override fun onScreenCall(callDetails: Call.Details) {
        val response = AndroidCallScreeningService.CallResponse.Builder()
        val handle = callDetails.handle
        val preferences = CallFilterPreferences(this)
        val filterEnabled = preferences.isEnabled()
        val isIncomingCall = callDetails.callDirection == Call.Details.DIRECTION_INCOMING
        val address = handle?.schemeSpecificPart
        val isKnownContact = filterEnabled && isIncomingCall && handle?.scheme == "tel" &&
            !address.isNullOrBlank() && ContactLookup(this).contains(address)
        when (
            CallScreeningPolicy.evaluate(
                isIncomingCall = isIncomingCall,
                handleScheme = handle?.scheme,
                address = address,
                filterEnabled = filterEnabled,
                mode = preferences.mode(),
                isKnownContact = isKnownContact,
            )
        ) {
            CallAction.ALLOW -> Unit
            CallAction.REJECT -> response.setDisallowCall(true).setRejectCall(true)
            CallAction.SILENCE -> response.setSilenceCall(true)
        }

        respondToCall(callDetails, response.build())
    }
}
