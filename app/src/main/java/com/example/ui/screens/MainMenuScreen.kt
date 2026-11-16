package com.example.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.HelpOutline
import androidx.compose.material.icons.automirrored.rounded.VolumeOff
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.rounded.GridView
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material.icons.rounded.MusicOff
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.game.AppUiState
import com.example.ui.components.ArrowEscapeLogoEmblem
import com.example.ui.components.PrimaryGlowButton
import com.example.ui.components.SecondaryGlassButton
import com.example.ui.theme.CardSurface
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.GridCellBorder
import com.example.ui.theme.HotPink
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.SolarYellow
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VividPurple

@Composable
fun SplashScreen(modifier: Modifier = Modifier) {
    val alpha = remember { Animatable(0f) }
    val scale = remember { Animatable(0.84f) }

    LaunchedEffect(Unit) {
        alpha.animateTo(1f, tween(450, easing = FastOutSlowInEasing))
        scale.animateTo(1f, tween(520, easing = FastOutSlowInEasing))
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .safeDrawingPadding(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .alpha(alpha.value)
                .scale(scale.value)
        ) {
            ArrowEscapeLogoEmblem(modifier = Modifier.size(104.dp))
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "ARROW ESCAPE",
                style = MaterialTheme.typography.displayMedium.copy(letterSpacing = 2.sp),
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Clear Every Path",
                style = MaterialTheme.typography.bodyMedium,
                color = ElectricCyan
            )
        }
    }
}

@Composable
fun MainMenuScreen(
    uiState: AppUiState,
    onPlayClick: () -> Unit,
    onLevelSelectClick: () -> Unit,
    onToggleSfx: () -> Unit,
    onToggleMusic: () -> Unit,
    onSettingsClick: () -> Unit,
    onHowToPlayClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val totalLevels = uiState.levelProgress.size.coerceAtLeast(10)
    val completedCount = uiState.completedLevelsCount
    val nextLevel = uiState.levelProgress.firstOrNull { it.unlocked && !it.completed }?.levelNumber
        ?: uiState.highestUnlockedLevel

    Column(
        modifier = modifier
            .fillMaxSize()
            .safeDrawingPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        // Logo Header
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.widthIn(max = 440.dp)
        ) {
            ArrowEscapeLogoEmblem(modifier = Modifier.size(86.dp))
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "ARROW ESCAPE",
                style = MaterialTheme.typography.displayMedium.copy(letterSpacing = 2.sp),
                color = TextPrimary,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Tap clear arrows in the right order",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Best Progress Indicator Card
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 440.dp)
                .testTag("best_progress_card"),
            shape = RoundedCornerShape(22.dp),
            color = CardSurface.copy(alpha = 0.88f),
            border = BorderStroke(1.dp, GridCellBorder)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "BEST PROGRESS",
                            style = MaterialTheme.typography.labelSmall,
                            color = ElectricCyan
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "$completedCount / $totalLevels Levels Cleared",
                            style = MaterialTheme.typography.titleLarge,
                            color = TextPrimary
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = SolarYellow.copy(alpha = 0.14f),
                        border = BorderStroke(1.dp, SolarYellow.copy(alpha = 0.4f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Star,
                                contentDescription = "Stars earned",
                                tint = SolarYellow,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "${uiState.totalStarsEarned} / ${uiState.maxStarsPossible}",
                                style = MaterialTheme.typography.labelLarge,
                                color = SolarYellow
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                LinearProgressIndicator(
                    progress = { completedCount.toFloat() / totalLevels.toFloat() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    color = ElectricCyan,
                    trackColor = GridCellBorder.copy(alpha = 0.55f),
                    strokeCap = StrokeCap.Round
                )
            }
        }

        Spacer(modifier = Modifier.height(22.dp))

        // Main Action Buttons
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 440.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            PrimaryGlowButton(
                text = if (completedCount == 0) "PLAY • LEVEL 1" else "PLAY • LEVEL $nextLevel",
                icon = Icons.Rounded.PlayArrow,
                onClick = onPlayClick,
                testTag = "menu_play_button",
                modifier = Modifier.fillMaxWidth(),
                gradientColors = listOf(ElectricCyan, ElectricBlue)
            )

            SecondaryGlassButton(
                text = "Level Selection",
                icon = Icons.Rounded.GridView,
                onClick = onLevelSelectClick,
                testTag = "menu_levels_button",
                modifier = Modifier.fillMaxWidth(),
                accentColor = VividPurple
            )

            // Quick Audio Toggles
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                QuickAudioToggleChip(
                    label = if (uiState.settings.sfxEnabled) "Sound: ON" else "Sound: OFF",
                    enabled = uiState.settings.sfxEnabled,
                    activeIcon = Icons.AutoMirrored.Rounded.VolumeUp,
                    inactiveIcon = Icons.AutoMirrored.Rounded.VolumeOff,
                    activeColor = NeonGreen,
                    onClick = onToggleSfx,
                    testTag = "menu_sound_toggle",
                    modifier = Modifier.weight(1f)
                )
                QuickAudioToggleChip(
                    label = if (uiState.settings.musicEnabled) "Music: ON" else "Music: OFF",
                    enabled = uiState.settings.musicEnabled,
                    activeIcon = Icons.Rounded.MusicNote,
                    inactiveIcon = Icons.Rounded.MusicOff,
                    activeColor = ElectricCyan,
                    onClick = onToggleMusic,
                    testTag = "menu_music_toggle",
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SecondaryGlassButton(
                    text = "How to Play",
                    icon = Icons.AutoMirrored.Rounded.HelpOutline,
                    onClick = onHowToPlayClick,
                    testTag = "menu_howto_button",
                    modifier = Modifier.weight(1f),
                    accentColor = HotPink
                )
                SecondaryGlassButton(
                    text = "Settings",
                    icon = Icons.Rounded.Settings,
                    onClick = onSettingsClick,
                    testTag = "menu_settings_button",
                    modifier = Modifier.weight(1f),
                    accentColor = SolarYellow
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))
        Text(
            text = "100% Offline • 10 Handcrafted Logic Levels",
            style = MaterialTheme.typography.labelMedium,
            color = TextMuted
        )
    }
}

@Composable
private fun QuickAudioToggleChip(
    label: String,
    enabled: Boolean,
    activeIcon: ImageVector,
    inactiveIcon: ImageVector,
    activeColor: Color,
    onClick: () -> Unit,
    testTag: String,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier
            .heightIn(min = 50.dp)
            .testTag(testTag),
        shape = RoundedCornerShape(16.dp),
        color = if (enabled) activeColor.copy(alpha = 0.14f) else CardSurface.copy(alpha = 0.8f),
        border = BorderStroke(
            width = 1.dp,
            color = if (enabled) activeColor.copy(alpha = 0.55f) else GridCellBorder
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = if (enabled) activeIcon else inactiveIcon,
                contentDescription = label,
                tint = if (enabled) activeColor else TextMuted,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                color = if (enabled) TextPrimary else TextSecondary
            )
        }
    }
}
