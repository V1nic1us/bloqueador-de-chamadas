package br.com.V1nic1us.callblocker

import android.Manifest
import android.content.Context
import android.view.accessibility.AccessibilityNodeInfo
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ApplicationProvider
import androidx.test.core.app.ActivityScenario
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ContactPermissionInstrumentedTest {
    @get:Rule val composeRule = createEmptyComposeRule()

    private val context = ApplicationProvider.getApplicationContext<Context>()
    private val preferences = CallFilterPreferences(context)
    private val automation = InstrumentationRegistry.getInstrumentation().uiAutomation
    private lateinit var scenario: ActivityScenario<MainActivity>

    @Before
    fun resetPermissionAndSettings() {
        preferences.setEnabled(false)
        automation.executeShellCommand(
            "pm revoke ${context.packageName} ${Manifest.permission.READ_CONTACTS}",
        ).close()
        scenario = ActivityScenario.launch(MainActivity::class.java)
    }

    @After
    fun clearSettings() {
        scenario.close()
        preferences.setEnabled(false)
    }

    @Test
    fun denyingContactsPermissionKeepsFilterDisabledAndExplainsWhy() {
        composeRule.onNodeWithText("Filtrar chamadas fora dos contatos").performClick()
        assertFalse(preferences.isEnabled())

        val denyButton = waitForDenyButton()
        check(denyButton.performAction(AccessibilityNodeInfo.ACTION_CLICK))

        composeRule.onNodeWithText("Filtrar chamadas fora dos contatos").assertIsOff()
        composeRule.onNodeWithText("Permita o acesso aos contatos para ativar o filtro.").assertExists()
        assertFalse(preferences.isEnabled())
    }

    private fun waitForDenyButton(): AccessibilityNodeInfo {
        repeat(50) {
            val root = automation.rootInActiveWindow
            val button = root?.findAccessibilityNodeInfosByViewId(
                "com.google.android.permissioncontroller:id/permission_deny_button",
            )?.firstOrNull() ?: root?.findAccessibilityNodeInfosByViewId(
                "com.android.permissioncontroller:id/permission_deny_button",
            )?.firstOrNull()
            if (button != null) return button
            Thread.sleep(100)
        }
        error("READ_CONTACTS denial button was not shown")
    }
}
