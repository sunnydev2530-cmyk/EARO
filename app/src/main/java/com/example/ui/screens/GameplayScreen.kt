package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Undo
import androidx.compose.material.icons.automirrored.rounded.VolumeOff
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.rounded.GridView
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Lightbulb
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.game.GameplayUiState
import com.example.game.LevelData
import com.example.ui.components.PrimaryGlowButton
import com.example.ui.components.PuzzleBoardCanvas
import com.example.ui.components.SecondaryGlassButton
import com.example.ui.components.VictoryOverlay
import com.example.ui.theme.CardSurface
import com.example.ui.theme.CardSurfaceElevated
import com.example.ui.theme.CoralRed
import com.example.ui.theme.DeepNavy
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.GridCellBorder
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.SolarYellow
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VividPurple

@Composable
fun GameplayScreen(
    gameplay: GameplayUiState,
    sfxEnabled: Boolean,
    isForeground: Boolean,
    onArrowTap: (Int) -> Unit,
    onUndoClick: () -> Unit,
    onHintClick: () -> Unit,
    onRestartClick: () -> Unit,
    onConfirmRestart: () -> Unit,
    onDismissRestart: () -> Unit,
    onPauseToggle: () -> Unit,
    onSoundToggle: () -> Unit,
    onNextLevel: () -> Unit,
    onLevelSelect: () -> Unit,
    onMainMenu: () -> Unit,
    onBackPressed: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler(onBack = onBackPressed)

    val animatedProgress by animateFloatAsState(
        targetValue = gameplay.progressFraction,
        animationSpec = tween(durationMillis = 280),
        label = "levelProgress"
    )

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top HUD
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 560.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Surface(
                    onClick = onPauseToggle,
                    modifier = Modifier
                        .size(48.dp)
                        .testTag("gameplay_pause_button"),
                    shape = CircleShape,
                    color = CardSurface,
                    border = BorderStroke(1.dp, GridCellBorder)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Rounded.Pause,
                            contentDescription = "Pause game",
                            tint = TextPrimary
                        )
                    }
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "LEVEL ${gameplay.level.number}",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = ElectricCyan
                    )
                    Text(
                        text = "${gameplay.level.title} • ${gameplay.level.difficultyLabel}",
                        style = MaterialTheme.typography.labelMedium,
                        color = TextSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Surface(
                        onClick = onSoundToggle,
                        modifier = Modifier
                            .size(48.dp)
                            .testTag("gameplay_sound_toggle"),
                        shape = CircleShape,
                        color = CardSurface,
                        border = BorderStroke(
                            1.dp,
                            if (sfxEnabled) NeonGreen.copy(alpha = 0.45f) else GridCellBorder
                        )
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = if (sfxEnabled) {
                                    Icons.AutoMirrored.Rounded.VolumeUp
                                } else {
                                    Icons.AutoMirrored.Rounded.VolumeOff
                                },
                                contentDescription = if (sfxEnabled) "Mute sound" else "Enable sound",
                                tint = if (sfxEnabled) NeonGreen else TextMuted
                            )
                        }
                    }

                    Surface(
                        onClick = onRestartClick,
                        modifier = Modifier
                            .size(48.dp)
                            .testTag("gameplay_top_restart_button"),
                        shape = CircleShape,
                        color = CardSurface,
                        border = BorderStroke(1.dp, GridCellBorder)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Rounded.Refresh,
                                contentDescription = "Restart level",
                                tint = TextPrimary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Progress & Remaining Arrows Card
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 560.dp),
                shape = RoundedCornerShape(16.dp),
                color = CardSurface.copy(alpha = 0.85f),
                border = BorderStroke(1.dp, GridCellBorder)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Remaining Arrows: ${gameplay.remainingCount} / ${gameplay.totalArrows}",
                            style = MaterialTheme.typography.labelLarge,
                            color = TextPrimary,
                            modifier = Modifier.testTag("remaining_arrows_text")
                        )
                        Text(
                            text = "Moves: ${gameplay.validMovesCount}",
                            style = MaterialTheme.typography.labelMedium,
                            color = TextSecondary
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { animatedProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(7.dp)
                            .clip(RoundedCornerShape(7.dp)),
                        color = ElectricCyan,
                        trackColor = GridCellBorder.copy(alpha = 0.5f),
                        strokeCap = StrokeCap.Round
                    )
                }
            }

            // Helpful Status Feedback Pill
            val statusText: Pair<String, Color> = when {
                gameplay.hintArrowId != null -> "Hint: Tap the highlighted yellow arrow!" to SolarYellow
                gameplay.blockedFeedback != null &&
                    System.currentTimeMillis() - gameplay.blockedFeedback.timestampMs < 2400L ->
                    "Path blocked! Clear the arrow in front first." to CoralRed
                else -> "Tap any arrow whose exit path is completely clear." to TextSecondary
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = statusText.first,
                style = MaterialTheme.typography.labelMedium,
                color = statusText.second,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            // Responsive Puzzle Board Area
            BoxWithConstraints(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                val boardRatio = gameplay.level.cols.toFloat() / gameplay.level.rows.toFloat()
                val availW = maxWidth
                val availH = maxHeight
                val targetW = if (availW / availH > boardRatio) {
                    availH * boardRatio
                } else {
                    availW
                }.coerceAtMost(540.dp)

                PuzzleBoardCanvas(
                    rows = gameplay.level.rows,
                    cols = gameplay.level.cols,
                    activeArrows = gameplay.activeArrows,
                    escapingArrows = gameplay.escapingArrows,
                    blockedFeedback = gameplay.blockedFeedback,
                    hintArrowId = gameplay.hintArrowId,
                    entranceNonce = gameplay.boardEntranceNonce,
                    isPaused = gameplay.isPaused || !isForeground,
                    onArrowTap = onArrowTap,
                    modifier = Modifier.width(targetW)
                )
            }

            // Bottom Action Controls
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 560.dp)
                    .padding(bottom = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                GameplayToolButton(
                    label = "Undo",
                    icon = Icons.AutoMirrored.Rounded.Undo,
                    enabled = gameplay.undoStack.isNotEmpty() && !gameplay.isCompleted,
                    accentColor = ElectricCyan,
                    onClick = onUndoClick,
                    testTag = "gameplay_undo_button",
                    modifier = Modifier.weight(1f)
                )
                GameplayToolButton(
                    label = "Hint (${gameplay.hintsRemaining})",
                    icon = Icons.Rounded.Lightbulb,
                    enabled = gameplay.hintsRemaining > 0 && gameplay.activeArrows.isNotEmpty() && !gameplay.isCompleted,
                    accentColor = SolarYellow,
                    onClick = onHintClick,
                    testTag = "gameplay_hint_button",
                    modifier = Modifier.weight(1f)
                )
                GameplayToolButton(
                    label = "Restart",
                    icon = Icons.Rounded.Refresh,
                    enabled = !gameplay.isCompleted,
                    accentColor = VividPurple,
                    onClick = onRestartClick,
                    testTag = "gameplay_restart_button",
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Pause Overlay
        AnimatedVisibility(
            visible = gameplay.isPaused && !gameplay.isCompleted,
            enter = fadeIn(tween(180)),
            exit = fadeOut(tween(150))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(DeepNavy.copy(alpha = 0.85f))
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 380.dp)
                        .testTag("pause_overlay"),
                    shape = RoundedCornerShape(26.dp),
                    color = CardSurface,
                    border = BorderStroke(1.2.dp, ElectricCyan.copy(alpha = 0.4f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "GAME PAUSED",
                            style = MaterialTheme.typography.headlineMedium,
                            color = ElectricCyan
                        )
                        Text(
                            text = "Level ${gameplay.level.number} • ${gameplay.level.title}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary
                        )
                        Spacer(modifier = Modifier.height(4.dp))

                        PrimaryGlowButton(
                            text = "Resume",
                            icon = Icons.Rounded.PlayArrow,
                            onClick = onPauseToggle,
                            testTag = "pause_resume_button",
                            modifier = Modifier.fillMaxWidth()
                        )
                        SecondaryGlassButton(
                            text = "Restart Level",
                            icon = Icons.Rounded.Refresh,
                            onClick = onConfirmRestart,
                            testTag = "pause_restart_button",
                            modifier = Modifier.fillMaxWidth(),
                            accentColor = SolarYellow
                        )
                        SecondaryGlassButton(
                            text = "Level Selection",
                            icon = Icons.Rounded.GridView,
                            onClick = onLevelSelect,
                            testTag = "pause_levels_button",
                            modifier = Modifier.fillMaxWidth(),
                            accentColor = VividPurple
                        )
                        SecondaryGlassButton(
                            text = "Main Menu",
                            icon = Icons.Rounded.Home,
                            onClick = onMainMenu,
                            testTag = "pause_home_button",
                            modifier = Modifier.fillMaxWidth(),
                            accentColor = ElectricCyan
                        )
                    }
                }
            }
        }

        // Victory Screen Overlay
        AnimatedVisibility(
            visible = gameplay.isCompleted,
            enter = fadeIn(tween(240)),
            exit = fadeOut(tween(150))
        ) {
            VictoryOverlay(
                levelNumber = gameplay.level.number,
                levelTitle = gameplay.level.title,
                isFinalLevel = gameplay.level.number >= LevelData.levels.size,
                arrowsCleared = gameplay.totalArrows,
                totalMoves = gameplay.validMovesCount + gameplay.blockedAttemptsCount,
                hintsUsed = gameplay.hintsUsed,
                earnedStars = gameplay.earnedStars,
                newlyUnlockedNext = gameplay.newlyUnlockedNextLevel,
                onNextLevel = onNextLevel,
                onReplay = onConfirmRestart,
                onHome = onMainMenu
            )
        }

        // Restart Confirmation Dialog
        if (gameplay.showRestartConfirm) {
            AlertDialog(
                onDismissRequest = onDismissRestart,
                containerColor = CardSurfaceElevated,
                titleContentColor = TextPrimary,
                textContentColor = TextSecondary,
                title = { Text("Restart Level ${gameplay.level.number}?") },
                text = { Text("This resets the current board to its starting arrows. Your unlocked levels and stars will stay saved.") },
                confirmButton = {
                    TextButton(
                        onClick = onConfirmRestart,
                        colors = ButtonDefaults.textButtonColors(contentColor = ElectricCyan),
                        modifier = Modifier.testTag("confirm_restart_yes")
                    ) {
                        Text("Restart")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = onDismissRestart,
                        colors = ButtonDefaults.textButtonColors(contentColor = TextSecondary),
                        modifier = Modifier.testTag("confirm_restart_cancel")
                    ) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
private fun GameplayToolButton(
    label: String,
    icon: ImageVector,
    enabled: Boolean,
    accentColor: Color,
    onClick: () -> Unit,
    testTag: String,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .heightIn(min = 52.dp)
            .testTag(testTag),
        shape = RoundedCornerShape(16.dp),
        color = if (enabled) CardSurface else CardSurface.copy(alpha = 0.45f),
        border = BorderStroke(
            width = 1.dp,
            color = if (enabled) accentColor.copy(alpha = 0.5f) else GridCellBorder.copy(alpha = 0.4f)
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (enabled) accentColor else TextMuted.copy(alpha = 0.5f),
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                color = if (enabled) TextPrimary else TextMuted.copy(alpha = 0.5f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
