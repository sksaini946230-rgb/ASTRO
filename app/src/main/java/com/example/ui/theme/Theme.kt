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
    primary = GoldPrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFF3E0),
    onPrimaryContainer = Color(0xFFE65100),
    secondary = SacredOrange,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFFF8E1),
    onSecondaryContainer = Color(0xFF1E293B),
    tertiary = AccentCyan,
    onTertiary = Color.White,
    background = Color(0xFFFAF7F2),
    onBackground = Color(0xFF1E293B),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF1E293B),
    surfaceVariant = Color(0xFFF1EDE6),
    onSurfaceVariant = Color(0xFF64748B),
    outline = Color(0xFFE2E8F0)
)

@Composable
fun AstroVedaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
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
