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

private val DarkColorScheme =
  darkColorScheme(
    primary = CyanAccent,
    onPrimary = Navy900,
    primaryContainer = TealPrimaryDark,
    onPrimaryContainer = Color.White,
    secondary = EmeraldSafe,
    onSecondary = Navy900,
    tertiary = AmberWarning,
    error = CoralSOS,
    background = DarkBackground,
    onBackground = Color.White,
    surface = DarkSurface,
    onSurface = Color.White,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = Color(0xFFCBD5E1),
    outline = Color(0xFF475569),
  )

private val LightColorScheme =
  lightColorScheme(
    primary = TealPrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE0F2F1),
    onPrimaryContainer = TealPrimaryDark,
    secondary = CyanAccent,
    onSecondary = Color.White,
    tertiary = AmberWarning,
    error = CoralSOS,
    background = LightBackground,
    onBackground = SlateTextPrimary,
    surface = LightSurface,
    onSurface = SlateTextPrimary,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = SlateTextSecondary,
    outline = SlateBorder,
  )

@Composable
fun SafeYatraTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }
      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  SafeYatraTheme(darkTheme = darkTheme, dynamicColor = dynamicColor, content = content)
}

