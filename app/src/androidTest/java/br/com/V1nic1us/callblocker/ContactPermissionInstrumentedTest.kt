package br.com.V1nic1us.callblocker

import android.Manifest
import android.content.Context
import android.view.accessibility.AccessibilityNodeInfo
import java.io.FileInputStream
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ApplicationProvider
import androidx.test.core.app.ActivityScenario
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertEquals
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
        runShell(
            "pm revoke ${context.packageName} ${Manifest.permission.READ_CONTACTS}",
        )
        runShell(
            "pm clear-permission-flags ${context.packageName} ${Manifest.permission.READ_CONTACTS} user-set user-fixed",
        )
        context.getSharedPreferences("contact_permission_requests", Context.MODE_PRIVATE)
            .edit().clear().commit()
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

    @Test
    fun permanentDenialRetryOpensAppSettingsGuidance() {
        composeRule.onNodeWithText("Filtrar chamadas fora dos contatos").performClick()
        check(waitForDenyButton().performAction(AccessibilityNodeInfo.ACTION_CLICK))
        composeRule.waitForIdle()
        runShell(
            "pm set-permission-flags ${context.packageName} ${Manifest.permission.READ_CONTACTS} user-fixed",
        )
        scenario.onActivity {
            assertFalse(it.shouldShowRequestPermissionRationale(Manifest.permission.READ_CONTACTS))
        }
        scenario.recreate()

        composeRule.onNodeWithText("Filtrar chamadas fora dos contatos").performClick()
        composeRule.onNodeWithText("Acesso aos contatos bloqueado. Abra as configurações do app para permitir.")
            .assertExists()
        assertFalse(preferences.isEnabled())
        composeRule.onNodeWithText("Abrir configurações do app").performClick()

        repeat(50) {
            val packageName = automation.rootInActiveWindow?.packageName?.toString()
            if (packageName == "com.android.settings") return
            Thread.sleep(100)
        }
        assertEquals("com.android.settings", automation.rootInActiveWindow?.packageName?.toString())
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

    private fun runShell(command: String) {
        automation.executeShellCommand(command).use { descriptor ->
            val output = FileInputStream(descriptor.fileDescriptor).bufferedReader().readText()
            check(output.isBlank()) { "$command: $output" }
        }
    }
}
