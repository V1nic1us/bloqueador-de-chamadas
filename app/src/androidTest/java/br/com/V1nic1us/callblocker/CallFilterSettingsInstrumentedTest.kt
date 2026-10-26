package br.com.V1nic1us.callblocker

import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isSelectable
import androidx.compose.ui.test.isToggleable
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ApplicationProvider
import android.content.Context
import android.Manifest
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class CallFilterSettingsInstrumentedTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    private val preferences by lazy {
        ApplicationProvider.getApplicationContext<Context>()
            .getSharedPreferences("call_filter_preferences", Context.MODE_PRIVATE)
    }

    @Before
    fun resetSettings() {
        preferences.edit().clear().commit()
        InstrumentationRegistry.getInstrumentation().uiAutomation
            .adoptShellPermissionIdentity(Manifest.permission.READ_CONTACTS)
        composeRule.activityRule.scenario.recreate()
    }

    @After
    fun clearSettings() {
        InstrumentationRegistry.getInstrumentation().uiAutomation.dropShellPermissionIdentity()
        preferences.edit().clear().commit()
    }

    @Test
    fun startsDisabledWithSilenceSelectedAndModeChoicesDisabled() {
        composeRule.onNodeWithText("Filtrar chamadas fora dos contatos")
            .assertIsOff()
        composeRule.onNode(hasText("Tocar em silêncio") and isSelectable())
            .assertIsSelected()
            .assertIsNotEnabled()
        composeRule.onNode(hasText("Recusar automaticamente") and isSelectable())
            .assertIsNotEnabled()
    }

    @Test
    fun enablingFilterAllowsChoosingAndPersistsRejectMode() {
        composeRule.onNodeWithText("Filtrar chamadas fora dos contatos").performClick()
        composeRule.onNodeWithText("Filtrar chamadas fora dos contatos").assertIsOn()
        composeRule.onNode(hasText("Recusar automaticamente") and isSelectable())
            .assertIsEnabled()
            .performClick()
            .assertIsSelected()

        composeRule.activityRule.scenario.recreate()

        composeRule.onNodeWithText("Filtrar chamadas fora dos contatos").assertIsOn()
        composeRule.onNode(hasText("Recusar automaticamente") and isSelectable())
            .assertIsSelected()
        composeRule.onNode(hasText("Tocar em silêncio") and isSelectable())
            .assertIsEnabled()
            .assertIsNotSelected()
    }

    @Test
    fun enablingFilterAllowsChoosingAndPersistsSilenceMode() {
        composeRule.onNodeWithText("Filtrar chamadas fora dos contatos").performClick()
        composeRule.onNode(hasText("Recusar automaticamente") and isSelectable()).performClick()
        composeRule.onNode(hasText("Tocar em silêncio") and isSelectable())
            .assertIsEnabled()
            .performClick()
            .assertIsSelected()

        composeRule.activityRule.scenario.recreate()

        composeRule.onNodeWithText("Filtrar chamadas fora dos contatos").assertIsOn()
        composeRule.onNode(hasText("Tocar em silêncio") and isSelectable())
            .assertIsSelected()
    }

    @Test
    fun disablingFilterDisablesModeChoicesWithoutChangingSavedMode() {
        composeRule.onNodeWithText("Filtrar chamadas fora dos contatos").performClick()
        composeRule.onNode(hasText("Recusar automaticamente") and isSelectable()).performClick()
        composeRule.onNodeWithText("Filtrar chamadas fora dos contatos").performClick()

        composeRule.onNodeWithText("Filtrar chamadas fora dos contatos").assertIsOff()
        composeRule.onNode(hasText("Recusar automaticamente") and isSelectable())
            .assertIsSelected()
            .assertIsNotEnabled()
    }
}
