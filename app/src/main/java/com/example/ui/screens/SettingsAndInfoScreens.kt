package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.automirrored.rounded.Undo
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.rounded.Block
import androidx.compose.material.icons.rounded.DeleteForever
import androidx.compose.material.icons.rounded.Lightbulb
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material.icons.rounded.PrivacyTip
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.Vibration
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.model.GameSettings
import com.example.ui.components.SecondaryGlassButton
import com.example.ui.theme.CardSurface
import com.example.ui.theme.CardSurfaceElevated
import com.example.ui.theme.CoralRed
import com.example.ui.theme.DeepNavy
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.GridCellBorder
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.SolarYellow
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VividPurple
import kotlin.math.roundToInt

@Composable
fun SettingsScreen(
    settings: GameSettings,
    showResetDialog: Boolean,
    onToggleSfx: () -> Unit,
    onToggleMusic: () -> Unit,
    onSfxVolumeChange: (Float) -> Unit,
    onMusicVolumeChange: (Float) -> Unit,
    onToggleVibration: () -> Unit,
    onRequestResetProgress: () -> Unit,
    onConfirmResetProgress: () -> Unit,
    onDismissResetProgress: () -> Unit,
    onOpenPrivacyPolicy: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler(onBack = onBack)

    Column(
        modifier = modifier
            .fillMaxSize()
            .safeDrawingPadding()
            .padding(horizontal = 18.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ScreenHeader(
            title = "Settings",
            onBack = onBack,
            backTestTag = "settings_back_button"
        )

        Spacer(modifier = Modifier.height(14.dp))

        Column(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 560.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Sound Effects Card
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = CardSurface,
                border = BorderStroke(1.dp, GridCellBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    SettingsSwitchRow(
                        icon = Icons.AutoMirrored.Rounded.VolumeUp,
                        iconTint = NeonGreen,
                        title = "Sound Effects",
                        subtitle = "Arrow escapes, blocked bumps, and chimes",
                        checked = settings.sfxEnabled,
                        onCheckedChange = { onToggleSfx() },
                        testTag = "settings_sfx_switch"
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    VolumeSliderRow(
                        label = "Effects Volume",
                        value = settings.sfxVolume,
                        enabled = settings.sfxEnabled,
                        accent = NeonGreen,
                        onValueChange = onSfxVolumeChange
                    )
                }
            }

            // Background Music Card
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = CardSurface,
                border = BorderStroke(1.dp, GridCellBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    SettingsSwitchRow(
                        icon = Icons.Rounded.MusicNote,
                        iconTint = ElectricCyan,
                        title = "Background Music",
                        subtitle = "Calm futuristic ambient synth loop",
                        checked = settings.musicEnabled,
                        onCheckedChange = { onToggleMusic() },
                        testTag = "settings_music_switch"
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    VolumeSliderRow(
                        label = "Music Volume",
                        value = settings.musicVolume,
                        enabled = settings.musicEnabled,
                        accent = ElectricCyan,
                        onValueChange = onMusicVolumeChange
                    )
                }
            }

            // Vibration Card
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = CardSurface,
                border = BorderStroke(1.dp, GridCellBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    SettingsSwitchRow(
                        icon = Icons.Rounded.Vibration,
                        iconTint = VividPurple,
                        title = "Haptic Vibration",
                        subtitle = "Subtle tactile pulses on escapes & blocks",
                        checked = settings.vibrationEnabled,
                        onCheckedChange = { onToggleVibration() },
                        testTag = "settings_vibration_switch"
                    )
                }
            }

            // Privacy Policy Button
            SecondaryGlassButton(
                text = "Privacy Policy",
                icon = Icons.Rounded.PrivacyTip,
                onClick = onOpenPrivacyPolicy,
                testTag = "settings_privacy_button",
                modifier = Modifier.fillMaxWidth(),
                accentColor = ElectricCyan
            )

            // Reset Progress Button
            SecondaryGlassButton(
                text = "Reset Game Progress",
                icon = Icons.Rounded.DeleteForever,
                onClick = onRequestResetProgress,
                testTag = "settings_reset_progress_button",
                modifier = Modifier.fillMaxWidth(),
                accentColor = CoralRed
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = onDismissResetProgress,
            containerColor = CardSurfaceElevated,
            titleContentColor = TextPrimary,
            textContentColor = TextSecondary,
            title = { Text("Reset All Progress?") },
            text = {
                Text("This will lock Levels 2–10 and clear all earned stars and best move records. Audio settings will stay unchanged.")
            },
            confirmButton = {
                TextButton(
                    onClick = onConfirmResetProgress,
                    colors = ButtonDefaults.textButtonColors(contentColor = CoralRed),
                    modifier = Modifier.testTag("confirm_reset_yes")
                ) {
                    Text("Reset")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = onDismissResetProgress,
                    colors = ButtonDefaults.textButtonColors(contentColor = TextSecondary),
                    modifier = Modifier.testTag("confirm_reset_cancel")
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun HowToPlayScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler(onBack = onBack)

    Column(
        modifier = modifier
            .fillMaxSize()
            .safeDrawingPadding()
            .padding(horizontal = 18.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ScreenHeader(
            title = "How to Play",
            onBack = onBack,
            backTestTag = "howto_back_button"
        )

        Spacer(modifier = Modifier.height(14.dp))

        Column(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 560.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            RuleInfoCard(
                icon = Icons.AutoMirrored.Rounded.ArrowForward,
                accent = ElectricCyan,
                title = "1. Tap an Arrow to Escape",
                description = "Every arrow points UP, DOWN, LEFT, or RIGHT. Tap an arrow whose straight-line path to the board edge is completely clear to send it flying off the board."
            )
            RuleInfoCard(
                icon = Icons.Rounded.Block,
                accent = CoralRed,
                title = "2. Watch for Blockers",
                description = "If another arrow sits anywhere in front of the tapped arrow, the path is blocked! Clear the blocking arrows first to open the escape lane."
            )
            RuleInfoCard(
                icon = Icons.AutoMirrored.Rounded.Undo,
                accent = VividPurple,
                title = "3. Use Undo & Smart Hints",
                description = "Made a move out of order? Tap Undo to restore your last escaped arrow. Stuck on a dense board? Tap Hint to highlight a guaranteed valid escape."
            )
            RuleInfoCard(
                icon = Icons.Rounded.Star,
                accent = SolarYellow,
                title = "4. Earn 3-Star Mastery",
                description = "Clear all arrows with 0 blocked taps and 0 hints to earn 3 Stars! Unlock all 10 handcrafted levels from Tutorial to the 42-arrow Omega Singularity."
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun PrivacyPolicyScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler(onBack = onBack)

    Column(
        modifier = modifier
            .fillMaxSize()
            .safeDrawingPadding()
            .padding(horizontal = 18.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ScreenHeader(
            title = "Privacy Policy",
            onBack = onBack,
            backTestTag = "privacy_back_button"
        )

        Spacer(modifier = Modifier.height(14.dp))

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 560.dp)
                .verticalScroll(rememberScrollState()),
            shape = RoundedCornerShape(20.dp),
            color = CardSurface,
            border = BorderStroke(1.dp, GridCellBorder)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Arrow Escape Puzzle — Offline Privacy Policy",
                    style = MaterialTheme.typography.titleLarge,
                    color = ElectricCyan
                )
                Text(
                    text = "1. No Personal Data Collection\nArrow Escape Puzzle operates 100% offline. The app does not collect, transmit, sell, or share any personal information, analytics, location data, contacts, or device identifiers.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
                Text(
                    text = "2. Local Device Storage Only\nYour level unlocks, star ratings, best move counts, and audio/vibration preferences are stored exclusively on your device using Android SharedPreferences. You can erase saved level progress anytime from Settings.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
                Text(
                    text = "3. Permissions\nThis app requests only the standard install-time VIBRATE permission for optional haptic feedback. No camera, microphone, storage, or network permissions are used.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            }
        }
    }
}

@Composable
private fun ScreenHeader(
    title: String,
    onBack: () -> Unit,
    backTestTag: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .widthIn(max = 560.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .size(48.dp)
                .testTag(backTestTag)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                contentDescription = "Back",
                tint = TextPrimary
            )
        }
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            color = TextPrimary
        )
    }
}

@Composable
private fun SettingsSwitchRow(
    icon: ImageVector,
    iconTint: Color,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    testTag: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            }
        }
        Spacer(modifier = Modifier.width(10.dp))
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            modifier = Modifier.testTag(testTag),
            colors = SwitchDefaults.colors(
                checkedThumbColor = DeepNavy,
                checkedTrackColor = iconTint
            )
        )
    }
}

@Composable
private fun VolumeSliderRow(
    label: String,
    value: Float,
    enabled: Boolean,
    accent: Color,
    onValueChange: (Float) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = if (enabled) TextSecondary else TextSecondary.copy(alpha = 0.5f)
            )
            Text(
                text = "${(value * 100f).roundToInt()}%",
                style = MaterialTheme.typography.labelMedium,
                color = if (enabled) accent else TextSecondary.copy(alpha = 0.5f)
            )
        }
        Slider(
            value = value,
            onValueChange = onValueChange,
            enabled = enabled,
            colors = SliderDefaults.colors(
                thumbColor = accent,
                activeTrackColor = accent,
                inactiveTrackColor = GridCellBorder
            )
        )
    }
}

@Composable
private fun RuleInfoCard(
    icon: ImageVector,
    accent: Color,
    title: String,
    description: String
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = CardSurface,
        border = BorderStroke(1.dp, accent.copy(alpha = 0.35f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = accent.copy(alpha = 0.16f),
                modifier = Modifier.size(44.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accent,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            }
        }
    }
}
