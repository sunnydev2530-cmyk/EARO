package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.StarOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.game.LevelData
import com.example.model.LevelDefinition
import com.example.model.LevelProgress
import com.example.ui.theme.CardSurface
import com.example.ui.theme.CardSurfaceElevated
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.GridCellBorder
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.SolarYellow
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun LevelSelectScreen(
    levelProgress: List<LevelProgress>,
    totalStarsEarned: Int,
    maxStarsPossible: Int,
    onSelectLevel: (Int) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler(onBack = onBack)

    Column(
        modifier = modifier
            .fillMaxSize()
            .safeDrawingPadding()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 600.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .size(48.dp)
                        .testTag("level_select_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = "Back to main menu",
                        tint = TextPrimary
                    )
                }
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Select Level",
                    style = MaterialTheme.typography.headlineMedium,
                    color = TextPrimary
                )
            }

            Surface(
                shape = RoundedCornerShape(50.dp),
                color = SolarYellow.copy(alpha = 0.14f),
                border = BorderStroke(1.dp, SolarYellow.copy(alpha = 0.4f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Star,
                        contentDescription = "Total stars",
                        tint = SolarYellow,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "$totalStarsEarned / $maxStarsPossible",
                        style = MaterialTheme.typography.labelLarge,
                        color = SolarYellow
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 152.dp),
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 640.dp),
            contentPadding = PaddingValues(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(LevelData.levels, key = { it.number }) { level ->
                val progress = levelProgress.firstOrNull { it.levelNumber == level.number }
                    ?: LevelProgress(
                        levelNumber = level.number,
                        unlocked = level.number == 1,
                        completed = false,
                        bestStars = 0
                    )
                LevelTileCard(
                    level = level,
                    progress = progress,
                    onClick = { onSelectLevel(level.number) }
                )
            }
        }
    }
}

@Composable
private fun LevelTileCard(
    level: LevelDefinition,
    progress: LevelProgress,
    onClick: () -> Unit
) {
    val isUnlocked = progress.unlocked
    val isCompleted = progress.completed
    val borderColor = when {
        isCompleted -> NeonGreen.copy(alpha = 0.55f)
        isUnlocked -> ElectricCyan.copy(alpha = 0.65f)
        else -> GridCellBorder.copy(alpha = 0.5f)
    }

    Surface(
        onClick = onClick,
        enabled = isUnlocked,
        modifier = Modifier
            .fillMaxWidth()
            .testTag("level_card_${level.number}"),
        shape = RoundedCornerShape(20.dp),
        color = if (isUnlocked) CardSurface else CardSurface.copy(alpha = 0.45f),
        border = BorderStroke(1.2.dp, borderColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (isUnlocked) CardSurfaceElevated else GridCellBorder.copy(alpha = 0.35f)
                ) {
                    Text(
                        text = "LVL ${level.number}",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                        color = if (isUnlocked) ElectricCyan else TextMuted,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }

                if (!isUnlocked) {
                    Icon(
                        imageVector = Icons.Rounded.Lock,
                        contentDescription = "Locked",
                        tint = TextMuted,
                        modifier = Modifier.size(20.dp)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Rounded.PlayArrow,
                        contentDescription = "Play level ${level.number}",
                        tint = if (isCompleted) NeonGreen else ElectricCyan,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = level.title,
                style = MaterialTheme.typography.titleMedium,
                color = if (isUnlocked) TextPrimary else TextMuted,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = "${level.difficultyLabel} • ${level.arrows.size} Arrows",
                style = MaterialTheme.typography.labelMedium,
                color = if (isUnlocked) TextSecondary else TextMuted.copy(alpha = 0.7f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                for (starIdx in 1..3) {
                    val lit = isCompleted && progress.bestStars >= starIdx
                    Icon(
                        imageVector = if (lit) Icons.Rounded.Star else Icons.Rounded.StarOutline,
                        contentDescription = null,
                        tint = when {
                            lit -> SolarYellow
                            isUnlocked -> TextMuted.copy(alpha = 0.55f)
                            else -> TextMuted.copy(alpha = 0.25f)
                        },
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}
