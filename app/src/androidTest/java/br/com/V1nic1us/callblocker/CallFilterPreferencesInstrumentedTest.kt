package br.com.V1nic1us.callblocker

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class CallFilterPreferencesInstrumentedTest {
    private lateinit var preferences: android.content.SharedPreferences
    private lateinit var subject: CallFilterPreferences

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        preferences = context.getSharedPreferences("call_filter_preferences", Context.MODE_PRIVATE)
        preferences.edit().clear().commit()
        subject = CallFilterPreferences(context)
    }

    @After
    fun tearDown() {
        preferences.edit().clear().commit()
    }

    @Test
    fun defaultsToDisabledAndSilenceMode() {
        assertFalse(subject.isEnabled())
        assertEquals(Mode.SILENCE, subject.mode())
    }

    @Test
    fun persistsEnabledStateAndSelectedMode() {
        subject.setEnabled(true)
        subject.setMode(Mode.REJECT)

        val reloaded = CallFilterPreferences(ApplicationProvider.getApplicationContext())
        assertTrue(reloaded.isEnabled())
        assertEquals(Mode.REJECT, reloaded.mode())
    }

    @Test
    fun invalidStoredValuesFallBackToSafeDefaults() {
        preferences.edit()
            .putString("enabled", "yes")
            .putInt("mode", 1)
            .commit()

        assertFalse(subject.isEnabled())
        assertEquals(Mode.SILENCE, subject.mode())
    }
}
