package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.theme.CardSurface
import com.example.ui.theme.DeepNavy
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.GridCellBorder
import com.example.ui.theme.HotPink
import com.example.ui.theme.MidnightPurple
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.VividPurple
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

private data class AmbientParticle(
    val baseX: Float,
    val baseY: Float,
    val radiusDp: Float,
    val speedX: Float,
    val speedY: Float,
    val phase: Float,
    val color: Color,
    val alpha: Float
)

@Composable
fun CosmicParticleBackground(
    isAnimating: Boolean,
    modifier: Modifier = Modifier
) {
    val particles = remember {
        val colors = listOf(ElectricCyan, ElectricBlue, VividPurple, HotPink, NeonGreen)
        List(18) { i ->
            AmbientParticle(
                baseX = ((i * 73 + 19) % 100) / 100f,
                baseY = ((i * 41 + 11) % 100) / 100f,
                radiusDp = 2.5f + (i % 4) * 1.4f,
                speedX = (((i % 5) - 2) * 0.008f).let { if (it == 0f) 0.006f else it },
                speedY = -0.012f - (i % 3) * 0.006f,
                phase = i * 0.7f,
                color = colors[i % colors.size],
                alpha = 0.16f + (i % 4) * 0.06f
            )
        }
    }

    var elapsedSec by remember { mutableLongStateOf(0L) }
    var animTimeSec = remember(elapsedSec) { elapsedSec / 1000f }

    LaunchedEffect(isAnimating) {
        if (!isAnimating) return@LaunchedEffect
        var lastFrame = withFrameMillis { it }
        while (true) {
            val now = withFrameMillis { it }
            val dt = (now - lastFrame).coerceIn(0L, 64L)
            lastFrame = now
            elapsedSec += dt
        }
    }

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        DeepNavy,
                        MidnightPurple,
                        Color(0xFF141132),
                        DeepNavy
                    )
                )
            )
    ) {
        val w = size.width
        val h = size.height

        // Subtle atmospheric radial nebula accents
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(VividPurple.copy(alpha = 0.14f), Color.Transparent),
                center = Offset(w * 0.18f, h * 0.16f),
                radius = w * 0.75f
            ),
            radius = w * 0.75f,
            center = Offset(w * 0.18f, h * 0.16f)
        )
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(ElectricCyan.copy(alpha = 0.10f), Color.Transparent),
                center = Offset(w * 0.86f, h * 0.78f),
                radius = w * 0.70f
            ),
            radius = w * 0.70f,
            center = Offset(w * 0.86f, h * 0.78f)
        )

        val t = animTimeSec
        for (p in particles) {
            val px = ((p.baseX + p.speedX * t + 0.02f * sin(t + p.phase)) % 1f + 1f) % 1f * w
            val py = ((p.baseY + p.speedY * t) % 1f + 1f) % 1f * h
            val r = p.radiusDp * density
            val pulse = 0.75f + 0.25f * sin(t * 1.5f + p.phase)

            drawCircle(
                color = p.color.copy(alpha = p.alpha * 0.35f * pulse),
                radius = r * 2.8f,
                center = Offset(px, py)
            )
            drawCircle(
                color = p.color.copy(alpha = p.alpha * pulse),
                radius = r,
                center = Offset(px, py)
            )
        }
    }
}

@Composable
fun ArrowEscapeLogoEmblem(
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        // Outer glowing rounded badge
        drawRoundRect(
            brush = Brush.linearGradient(
                colors = listOf(Color(0xFF1A1F48), Color(0xFF10132C))
            ),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(w * 0.26f, w * 0.26f)
        )
        drawRoundRect(
            brush = Brush.linearGradient(
                colors = listOf(ElectricCyan.copy(alpha = 0.7f), VividPurple.copy(alpha = 0.7f))
            ),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(w * 0.26f, w * 0.26f),
            style = Stroke(width = w * 0.03f)
        )

        fun drawMiniArrow(
            cx: Float,
            cy: Float,
            angleDeg: Float,
            len: Float,
            color: Color
        ) {
            val rad = angleDeg * (PI / 180.0).toFloat()
            val cosA = cos(rad)
            val sinA = sin(rad)

            fun rot(x: Float, y: Float): Offset =
                Offset(cx + x * cosA - y * sinA, cy + x * sinA + y * cosA)

            val tail = rot(-len * 0.48f, 0f)
            val neck = rot(len * 0.18f, 0f)
            val tip = rot(len * 0.52f, 0f)
            val leftWing = rot(len * 0.12f, -len * 0.30f)
            val rightWing = rot(len * 0.12f, len * 0.30f)

            drawLine(
                color = color.copy(alpha = 0.35f),
                start = tail,
                end = neck,
                strokeWidth = len * 0.28f,
                cap = StrokeCap.Round
            )
            drawLine(
                color = color,
                start = tail,
                end = neck,
                strokeWidth = len * 0.16f,
                cap = StrokeCap.Round
            )

            val head = Path().apply {
                moveTo(tip.x, tip.y)
                lineTo(leftWing.x, leftWing.y)
                lineTo(rightWing.x, rightWing.y)
                close()
            }
            drawPath(path = head, color = color)
            drawPath(
                path = head,
                color = color,
                style = Stroke(width = len * 0.08f, join = StrokeJoin.Round)
            )
        }

        drawMiniArrow(w * 0.33f, h * 0.34f, -135f, w * 0.34f, HotPink)
        drawMiniArrow(w * 0.33f, h * 0.68f, 135f, w * 0.34f, ElectricBlue)
        drawMiniArrow(w * 0.62f, h * 0.49f, -45f, w * 0.44f, ElectricCyan)
    }
}

@Composable
fun PrimaryGlowButton(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit,
    testTag: String,
    modifier: Modifier = Modifier,
    gradientColors: List<Color> = listOf(ElectricCyan, ElectricBlue)
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.96f else 1f,
        animationSpec = spring(dampingRatio = 0.65f, stiffness = 500f),
        label = "btnScale"
    )

    Box(
        modifier = modifier
            .scale(scale)
            .heightIn(min = 56.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(Brush.horizontalGradient(gradientColors))
            .border(
                width = 1.dp,
                color = Color.White.copy(alpha = 0.25f),
                shape = RoundedCornerShape(18.dp)
            )
            .clickable(
                interactionSource = interactionSource,
                indication = androidx.compose.material3.ripple(color = Color.White),
                onClick = onClick
            )
            .testTag(testTag),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = DeepNavy,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = DeepNavy
            )
        }
    }
}

@Composable
fun SecondaryGlassButton(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit,
    testTag: String,
    modifier: Modifier = Modifier,
    accentColor: Color = ElectricCyan
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.96f else 1f,
        animationSpec = spring(dampingRatio = 0.65f, stiffness = 500f),
        label = "secBtnScale"
    )

    Surface(
        onClick = onClick,
        modifier = modifier
            .scale(scale)
            .heightIn(min = 52.dp)
            .testTag(testTag),
        shape = RoundedCornerShape(16.dp),
        color = CardSurface.copy(alpha = 0.88f),
        border = BorderStroke(1.dp, GridCellBorder),
        interactionSource = interactionSource
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = accentColor,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge,
                color = TextPrimary
            )
        }
    }
}
