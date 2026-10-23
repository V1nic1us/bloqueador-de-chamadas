package br.com.V1nic1us.callblocker.theme

import androidx.compose.ui.graphics.toArgb
import org.junit.Assert.assertEquals
import org.junit.Test

class Material3TokensTest {
    @Test
    fun lightFallbackUsesTheAppPrimaryAndSemanticSurfaceColors() {
        assertEquals(0xFF006874.toInt(), AppLightColorScheme.primary.toArgb())
        assertEquals(0xFFFFFFFF.toInt(), AppLightColorScheme.onPrimary.toArgb())
        assertEquals(0xFFF4FAFB.toInt(), AppLightColorScheme.background.toArgb())
        assertEquals(0xFF161D1F.toInt(), AppLightColorScheme.onBackground.toArgb())
    }

    @Test
    fun darkFallbackUsesTheAppPrimaryAndSemanticSurfaceColors() {
        assertEquals(0xFF83D2E2.toInt(), AppDarkColorScheme.primary.toArgb())
        assertEquals(0xFF00363D.toInt(), AppDarkColorScheme.onPrimary.toArgb())
        assertEquals(0xFF0E1416.toInt(), AppDarkColorScheme.background.toArgb())
        assertEquals(0xFFDEE3E5.toInt(), AppDarkColorScheme.onBackground.toArgb())
    }
}
