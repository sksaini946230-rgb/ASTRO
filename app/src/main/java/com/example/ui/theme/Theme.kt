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

private val AstroDarkColorScheme = darkColorScheme(
    primary = GoldPrimary,
    onPrimary = CosmicDeepNavy,
    primaryContainer = CosmicCardSurface,
    onPrimaryContainer = TextGold,
    secondary = SacredOrange,
    onSecondary = Color.White,
    secondaryContainer = CosmicDarkBlue,
    onSecondaryContainer = TextPrimaryDark,
    tertiary = AccentCyan,
    onTertiary = CosmicDeepNavy,
    background = CosmicDeepNavy,
    onBackground = TextPrimaryDark,
    surface = CosmicDarkBlue,
    onSurface = TextPrimaryDark,
    surfaceVariant = CosmicCardSurface,
    onSurfaceVariant = TextSecondaryDark,
    outline = GlassBorder
)

private val AstroLightColorScheme = lightColorScheme(
    primary = GoldSecondary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFF8E1),
    onPrimaryContainer = Color(0xFF5D4037),
    secondary = SacredOrange,
    onSecondary = Color.White,
    background = Color(0xFFFBF9F5),
    onBackground = Color(0xFF1C1B1F),
    surface = Color.White,
    onSurface = Color(0xFF1C1B1F),
    surfaceVariant = Color(0xFFF5F0E6),
    onSurfaceVariant = Color(0xFF4A453E),
    outline = GoldSecondary
)

@Composable
fun AstroVedaTheme(
    darkTheme: Boolean = true, // Default to rich cosmic dark theme for luxury Vedic aesthetic
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> AstroDarkColorScheme
        else -> AstroLightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
