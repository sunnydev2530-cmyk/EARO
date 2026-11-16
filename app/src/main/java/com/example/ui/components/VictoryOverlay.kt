package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.StarOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.ui.theme.CardSurface
import com.example.ui.theme.CardSurfaceElevated
import com.example.ui.theme.DeepNavy
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
import com.example.ui.theme.WarmOrange
import kotlinx.coroutines.delay
import kotlin.math.sin

private data class ConfettiPiece(
    val xFrac: Float,
    val startY: Float,
    val speedY: Float,
    val swayAmp: Float,
    val swayFreq: Float,
    val rotSpeed: Float,
    val sizeDp: Float,
    val color: Color
)

@Composable
fun VictoryOverlay(
    levelNumber: Int,
    levelTitle: String,
    isFinalLevel: Boolean,
    arrowsCleared: Int,
    totalMoves: Int,
    hintsUsed: Int,
    earnedStars: Int,
    newlyUnlockedNext: Boolean,
    onNextLevel: () -> Unit,
    onReplay: () -> Unit,
    onHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    val confetti = remember {
        val colors = listOf(ElectricCyan, HotPink, SolarYellow, NeonGreen, VividPurple, WarmOrange, ElectricBlue)
        List(36) { i ->
            ConfettiPiece(
                xFrac = ((i * 29 + 7) % 96 + 2) / 100f,
                startY = -0.08f - (i % 6) * 0.09f,
                speedY = 0.22f + (i % 5) * 0.055f,
                swayAmp = 0.025f + (i % 3) * 0.01f,
                swayFreq = 2.0f + (i % 4) * 0.6f,
                rotSpeed = (if (i % 2 == 0) 1f else -1f) * (90f + (i % 4) * 45f),
                sizeDp = 7f + (i % 3) * 3f,
                color = colors[i % colors.size]
            )
        }
    }

    var elapsedSec by remember { mutableFloatStateOf(0f) }
    LaunchedEffect(Unit) {
        var last = withFrameMillis { it }
        while (elapsedSec < 5.5f) {
            val now = withFrameMillis { it }
            val dt = ((now - last).coerceIn(0L, 64L)) / 1000f
            last = now
            elapsedSec += dt
        }
    }

    val starScales = remember { List(3) { Animatable(0.2f) } }
    LaunchedEffect(earnedStars) {
        for (i in 0 until 3) {
            delay(130L)
            starScales[i].animateTo(
                targetValue = if (i < earnedStars) 1.15f else 0.9f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                )
            )
            if (i < earnedStars) {
                starScales[i].animateTo(1f, spring())
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DeepNavy.copy(alpha = 0.84f))
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        // Lightweight confetti layer
        Canvas(modifier = Modifier.fillMaxSize()) {
            val t = elapsedSec
            val fadeAlpha = ((5.5f - t) / 1.2f).coerceIn(0f, 1f)
            if (fadeAlpha > 0f) {
                for (c in confetti) {
                    val y = (c.startY + c.speedY * t)
                    if (y in -0.1f..1.1f) {
                        val x = c.xFrac + sin(t * c.swayFreq + c.xFrac * 10f) * c.swayAmp
                        val px = x * size.width
                        val py = y * size.height
                        val s = c.sizeDp * density
                        rotate(degrees = t * c.rotSpeed, pivot = Offset(px, py)) {
                            drawRoundRect(
                                color = c.color.copy(alpha = 0.9f * fadeAlpha),
                                topLeft = Offset(px - s * 0.5f, py - s * 0.28f),
                                size = Size(s, s * 0.56f),
                                cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx())
                            )
                        }
                    }
                }
            }
        }

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 420.dp)
                .testTag("victory_dialog"),
            shape = RoundedCornerShape(28.dp),
            color = CardSurface,
            border = BorderStroke(1.5.dp, ElectricCyan.copy(alpha = 0.45f)),
            tonalElevation = 12.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (isFinalLevel) "ALL LEVELS MASTERED!" else "LEVEL COMPLETED!",
                    style = MaterialTheme.typography.headlineMedium,
                    color = ElectricCyan,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Level $levelNumber • $levelTitle",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )

                if (newlyUnlockedNext && !isFinalLevel) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Surface(
                        shape = RoundedCornerShape(50.dp),
                        color = NeonGreen.copy(alpha = 0.16f),
                        border = BorderStroke(1.dp, NeonGreen.copy(alpha = 0.45f))
                    ) {
                        Text(
                            text = "Level ${levelNumber + 1} Unlocked!",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = NeonGreen,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Animated 3-Star Rating
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    for (i in 0 until 3) {
                        val lit = i < earnedStars
                        Icon(
                            imageVector = if (lit) Icons.Rounded.Star else Icons.Rounded.StarOutline,
                            contentDescription = if (lit) "Earned star ${i + 1}" else "Empty star ${i + 1}",
                            tint = if (lit) SolarYellow else TextMuted.copy(alpha = 0.5f),
                            modifier = Modifier
                                .size(48.dp)
                                .scale(starScales[i].value)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = when (earnedStars) {
                        3 -> "Flawless Precision • 3 / 3 Stars"
                        2 -> "Great Logic • 2 / 3 Stars"
                        else -> "Puzzle Cleared • 1 / 3 Stars"
                    },
                    style = MaterialTheme.typography.labelLarge,
                    color = SolarYellow
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Stats card
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    color = CardSurfaceElevated,
                    border = BorderStroke(1.dp, GridCellBorder)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 14.dp, horizontal = 12.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        VictoryStatItem(label = "Cleared", value = "$arrowsCleared")
                        VictoryStatItem(label = "Moves", value = "$totalMoves")
                        VictoryStatItem(label = "Hints", value = "$hintsUsed")
                    }
                }

                Spacer(modifier = Modifier.height(22.dp))

                PrimaryGlowButton(
                    text = if (isFinalLevel) "Level Select" else "Next Level",
                    icon = Icons.AutoMirrored.Rounded.ArrowForward,
                    onClick = onNextLevel,
                    testTag = "victory_next_button",
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SecondaryGlassButton(
                        text = "Replay",
                        icon = Icons.Rounded.Refresh,
                        onClick = onReplay,
                        testTag = "victory_replay_button",
                        modifier = Modifier.weight(1f),
                        accentColor = ElectricCyan
                    )
                    SecondaryGlassButton(
                        text = "Home",
                        icon = Icons.Rounded.Home,
                        onClick = onHome,
                        testTag = "victory_home_button",
                        modifier = Modifier.weight(1f),
                        accentColor = VividPurple
                    )
                }
            }
        }
    }
}

@Composable
private fun VictoryStatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineMedium,
            color = TextPrimary
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = TextSecondary
        )
    }
}
