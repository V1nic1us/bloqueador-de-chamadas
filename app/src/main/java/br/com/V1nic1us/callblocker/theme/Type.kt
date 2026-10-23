package br.com.V1nic1us.callblocker.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val Typography = Typography(
    displayLarge = materialTextStyle(57, 64, FontWeight.Normal, (-0.25).sp),
    displayMedium = materialTextStyle(45, 52, FontWeight.Normal),
    displaySmall = materialTextStyle(36, 44, FontWeight.Normal),
    headlineLarge = materialTextStyle(32, 40, FontWeight.Normal),
    headlineMedium = materialTextStyle(28, 36, FontWeight.Normal),
    headlineSmall = materialTextStyle(24, 32, FontWeight.Normal),
    titleLarge = materialTextStyle(22, 28, FontWeight.Normal),
    titleMedium = materialTextStyle(16, 24, FontWeight.Medium, 0.15.sp),
    titleSmall = materialTextStyle(14, 20, FontWeight.Medium, 0.1.sp),
    bodyLarge = materialTextStyle(16, 24, FontWeight.Normal, 0.5.sp),
    bodyMedium = materialTextStyle(14, 20, FontWeight.Normal, 0.25.sp),
    bodySmall = materialTextStyle(12, 16, FontWeight.Normal, 0.4.sp),
    labelLarge = materialTextStyle(14, 20, FontWeight.Medium, 0.1.sp),
    labelMedium = materialTextStyle(12, 16, FontWeight.Medium, 0.5.sp),
    labelSmall = materialTextStyle(11, 16, FontWeight.Medium, 0.5.sp),
)

private fun materialTextStyle(
    fontSize: Int,
    lineHeight: Int,
    fontWeight: FontWeight,
    letterSpacing: androidx.compose.ui.unit.TextUnit = 0.sp,
) = TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = fontWeight,
    fontSize = fontSize.sp,
    lineHeight = lineHeight.sp,
    letterSpacing = letterSpacing,
)
