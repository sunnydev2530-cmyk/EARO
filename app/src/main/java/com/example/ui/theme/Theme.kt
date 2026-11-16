package com.example.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

private val ArrowEscapeColorScheme = darkColorScheme(
    primary = ElectricCyan,
    onPrimary = DeepNavy,
    primaryContainer = CardSurfaceElevated,
    onPrimaryContainer = ElectricCyan,
    secondary = VividPurple,
    onSecondary = TextPrimary,
    secondaryContainer = CardSurface,
    onSecondaryContainer = TextPrimary,
    tertiary = HotPink,
    onTertiary = TextPrimary,
    background = DeepNavy,
    onBackground = TextPrimary,
    surface = MidnightPurple,
    onSurface = TextPrimary,
    surfaceVariant = CardSurface,
    onSurfaceVariant = TextSecondary,
    outline = GridCellBorder,
    error = CoralRed,
    onError = TextPrimary
)

private val AppShapes = Shapes(
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(18.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(30.dp)
)

@Composable
fun ArrowEscapeTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = ArrowEscapeColorScheme,
        typography = AppTypography,
        shapes = AppShapes,
        content = content
    )
}
