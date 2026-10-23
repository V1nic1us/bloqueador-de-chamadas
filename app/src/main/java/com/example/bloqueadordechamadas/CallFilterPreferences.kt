package com.example.bloqueadordechamadas

import android.content.Context

enum class Mode {
    REJECT,
    SILENCE,
}

class CallFilterPreferences(context: Context) {
    private val preferences = context.applicationContext.getSharedPreferences(
        "call_filter_preferences",
        Context.MODE_PRIVATE,
    )

    fun isEnabled(): Boolean = preferences.getBoolean(KEY_ENABLED, false)

    fun mode(): Mode = when (try {
        preferences.getString(KEY_MODE, null)
    } catch (_: ClassCastException) {
        null
    }) {
        Mode.REJECT.name -> Mode.REJECT
        else -> Mode.SILENCE
    }

    fun setEnabled(enabled: Boolean) {
        preferences.edit().putBoolean(KEY_ENABLED, enabled).apply()
    }

    fun setMode(mode: Mode) {
        preferences.edit().putString(KEY_MODE, mode.name).apply()
    }

    private companion object {
        const val KEY_ENABLED = "enabled"
        const val KEY_MODE = "mode"
    }
}
