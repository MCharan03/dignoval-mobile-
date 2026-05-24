package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val SentinelColorScheme =
  darkColorScheme(
      primary = SentinelPrimary,
      secondary = SentinelSecondary,
      tertiary = SentinelSurfaceVariant,
      background = SentinelBackground,
      surface = SentinelSurface,
      onPrimary = SentinelBackground,
      onSecondary = SentinelText,
      onBackground = SentinelText,
      onSurface = SentinelText,
      error = SentinelError,
      onError = Color.White,
      surfaceVariant = SentinelSurfaceVariant,
      onSurfaceVariant = SentinelText,
      outline = SentinelBorder
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = true, // Force dark theme vibe
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  MaterialTheme(colorScheme = SentinelColorScheme, typography = Typography, content = content)
}
