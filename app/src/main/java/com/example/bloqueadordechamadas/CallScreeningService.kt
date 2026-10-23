package com.example.bloqueadordechamadas

import android.telecom.Call
import android.telecom.CallScreeningService as AndroidCallScreeningService

class CallScreeningService : AndroidCallScreeningService() {
    override fun onScreenCall(callDetails: Call.Details) {
        val response = AndroidCallScreeningService.CallResponse.Builder()
        val handle = callDetails.handle
        val isIncomingTelephoneCall = callDetails.callDirection == Call.Details.DIRECTION_INCOMING &&
            handle?.scheme == "tel" &&
            !handle.schemeSpecificPart.isNullOrBlank()

        val preferences = CallFilterPreferences(this)
        if (isIncomingTelephoneCall && preferences.isEnabled()) {
            when (preferences.mode()) {
                Mode.REJECT -> response.setDisallowCall(true).setRejectCall(true)
                Mode.SILENCE -> response.setSilenceCall(true)
            }
        }

        respondToCall(callDetails, response.build())
    }
}
