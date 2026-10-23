package br.com.V1nic1us.callblocker

import org.junit.Assert.assertEquals
import org.junit.Test

class CallScreeningPolicyTest {
    @Test
    fun `allows calls when filtering is disabled`() {
        assertEquals(
            CallAction.ALLOW,
            CallScreeningPolicy.evaluate(
                isIncomingCall = true,
                handleScheme = "tel",
                address = "+5511999999999",
                filterEnabled = false,
                mode = Mode.REJECT,
            ),
        )
    }

    @Test
    fun `reject mode rejects incoming telephone calls`() {
        assertEquals(
            CallAction.REJECT,
            CallScreeningPolicy.evaluate(
                isIncomingCall = true,
                handleScheme = "tel",
                address = "+5511999999999",
                filterEnabled = true,
                mode = Mode.REJECT,
            ),
        )
    }

    @Test
    fun `silence mode silences incoming telephone calls`() {
        assertEquals(
            CallAction.SILENCE,
            CallScreeningPolicy.evaluate(
                isIncomingCall = true,
                handleScheme = "tel",
                address = "+5511999999999",
                filterEnabled = true,
                mode = Mode.SILENCE,
            ),
        )
    }

    @Test
    fun `allows outgoing calls`() {
        assertActionAllowed(isIncomingCall = false, scheme = "tel", address = "+5511999999999")
    }

    @Test
    fun `allows calls without a telephone handle`() {
        assertActionAllowed(isIncomingCall = true, scheme = null, address = null)
    }

    @Test
    fun `allows non telephone handles`() {
        assertActionAllowed(isIncomingCall = true, scheme = "sip", address = "alice@example.com")
    }

    @Test
    fun `allows telephone handles with a blank address`() {
        assertActionAllowed(isIncomingCall = true, scheme = "tel", address = "  ")
    }

    private fun assertActionAllowed(isIncomingCall: Boolean, scheme: String?, address: String?) {
        assertEquals(
            CallAction.ALLOW,
            CallScreeningPolicy.evaluate(
                isIncomingCall = isIncomingCall,
                handleScheme = scheme,
                address = address,
                filterEnabled = true,
                mode = Mode.REJECT,
            ),
        )
    }
}
