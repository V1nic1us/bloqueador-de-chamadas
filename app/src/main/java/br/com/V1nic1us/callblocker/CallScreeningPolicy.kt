package br.com.V1nic1us.callblocker

/** The action the screening service should take for an incoming call. */
enum class CallAction {
    ALLOW,
    REJECT,
    SILENCE,
}

/** Pure call filtering rules, kept separate from Android Telecom for unit testing. */
object CallScreeningPolicy {
    fun evaluate(
        isIncomingCall: Boolean,
        handleScheme: String?,
        address: String?,
        filterEnabled: Boolean,
        mode: Mode,
        isKnownContact: Boolean = false,
    ): CallAction {
        if (!filterEnabled || !isIncomingCall || handleScheme != "tel" || address.isNullOrBlank()) {
            return CallAction.ALLOW
        }

        if (isKnownContact) return CallAction.ALLOW

        return when (mode) {
            Mode.REJECT -> CallAction.REJECT
            Mode.SILENCE -> CallAction.SILENCE
        }
    }
}
