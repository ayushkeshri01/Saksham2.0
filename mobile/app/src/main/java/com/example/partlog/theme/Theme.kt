package com.example.partlog.theme

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

private val DarkColorScheme = darkColorScheme(
    primary = RoyalBlue,
    secondary = CyanAccent,
    tertiary = CyanAccent,
    background = SoftBlueBackground,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = DarkGrey,
    onSurface = DarkGrey
)

private val LightColorScheme = lightColorScheme(
    primary = RoyalBlue,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFEAF0F8),
    onPrimaryContainer = RoyalBlue,
    secondary = CyanAccent,
    onSecondary = Color.White,
    background = SoftBlueBackground,
    onBackground = DarkGrey,
    onSurface = DarkGrey,
    surface = Color.White,
    surfaceVariant = Color(0xFFEAF0F8),
    onSurfaceVariant = LightGrey
)

@Composable
fun PartLogTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  // Disabling dynamic colors by default to preserve custom branding
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
