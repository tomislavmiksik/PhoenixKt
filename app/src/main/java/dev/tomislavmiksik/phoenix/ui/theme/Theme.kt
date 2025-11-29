package dev.tomislavmiksik.phoenix.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * Light color scheme with orange (#FF8427) as the primary color.
 * This app uses light mode only.
 */
private val LightColorScheme = lightColorScheme(
    // Primary colors - Orange (#FF8427)
    primary = Orange,
    onPrimary = Color.White,
    primaryContainer = OrangeLight,
    onPrimaryContainer = OrangeDark,

    // Secondary colors - Blue Grey
    secondary = BlueGrey,
    onSecondary = Color.White,
    secondaryContainer = BlueGreyLight,
    onSecondaryContainer = BlueGreyDark,

    // Tertiary colors - Amber
    tertiary = Amber,
    onTertiary = DarkGrey,
    tertiaryContainer = AmberLight,
    onTertiaryContainer = AmberDark,

    // Error colors
    error = Color(0xFFB00020),
    onError = Color.White,
    errorContainer = Color(0xFFFFDAD4),
    onErrorContainer = Color(0xFF410002),

    // Background colors
    background = Color.White,
    onBackground = DarkGrey,

    // Surface colors
    surface = Color.White,
    onSurface = DarkGrey,
    surfaceVariant = LightGrey,
    onSurfaceVariant = NeutralGrey,

    // Outline
    outline = NeutralGrey,
    outlineVariant = LightGrey,
)

/**
 * Phoenix theme with light mode only.
 * Uses orange (#FF8427) as the primary brand color.
 */
@Composable
fun PhoenixTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}
