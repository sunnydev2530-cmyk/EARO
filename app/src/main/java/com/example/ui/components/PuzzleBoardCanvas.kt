package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.game.BlockedFeedback
import com.example.game.EscapingArrowState
import com.example.game.PuzzleEngine
import com.example.model.ArrowPiece
import com.example.model.Direction
import com.example.ui.theme.BoardFrameColor
import com.example.ui.theme.CoralRed
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.GridCellBorder
import com.example.ui.theme.GridCellColor
import com.example.ui.theme.SolarYellow
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

private data class EscapeBurstParticle(
    val originX: Float,
    val originY: Float,
    val vx: Float,
    val vy: Float,
    val color: Color,
    val radius: Float,
    val bornAtMs: Long
)

@Composable
fun PuzzleBoardCanvas(
    rows: Int,
    cols: Int,
    activeArrows: List<ArrowPiece>,
    escapingArrows: List<EscapingArrowState>,
    blockedFeedback: BlockedFeedback?,
    hintArrowId: Int?,
    entranceNonce: Int,
    isPaused: Boolean,
    onArrowTap: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val entranceScale = remember(entranceNonce) { Animatable(0.86f) }
    LaunchedEffect(entranceNonce) {
        entranceScale.snapTo(0.86f)
        entranceScale.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 260, easing = FastOutSlowInEasing)
        )
    }

    val burstParticles = remember { mutableStateListOf<EscapeBurstParticle>() }
    val seenEscapes = remember(entranceNonce) { HashSet<Long>() }

    // Frame clock active for hint pulse, escape animations, blocked shake, or particle bursts
    var frameNowMs by remember { mutableLongStateOf(System.currentTimeMillis()) }

    LaunchedEffect(escapingArrows, entranceNonce) {
        for (esc in escapingArrows) {
            val key = esc.piece.id.toLong() * 1000000L + (esc.startTimeMs % 1000000L)
            if (seenEscapes.add(key)) {
                val now = System.currentTimeMillis()
                val angles = 10
                for (i in 0 until angles) {
                    val theta = (2.0 * PI * i / angles) + (esc.piece.id % 3) * 0.2
                    val speed = 0.9f + (i % 3) * 0.45f
                    burstParticles.add(
                        EscapeBurstParticle(
                            originX = esc.piece.col + 0.5f,
                            originY = esc.piece.row + 0.5f,
                            vx = cos(theta).toFloat() * speed,
                            vy = sin(theta).toFloat() * speed,
                            color = esc.piece.color.fill,
                            radius = 3.5f + (i % 3) * 1.5f,
                            bornAtMs = now
                        )
                    )
                }
            }
        }
    }

    val hasActiveAnimation = !isPaused && (
        escapingArrows.isNotEmpty() ||
            blockedFeedback != null ||
            hintArrowId != null ||
            burstParticles.isNotEmpty()
        )

    LaunchedEffect(hasActiveAnimation) {
        if (!hasActiveAnimation) {
            frameNowMs = System.currentTimeMillis()
            return@LaunchedEffect
        }
        while (true) {
            withFrameMillis {
                val now = System.currentTimeMillis()
                frameNowMs = now
                burstParticles.removeAll { now - it.bornAtMs > 420L }
            }
        }
    }

    Canvas(
        modifier = modifier
            .aspectRatio(cols.toFloat() / rows.toFloat())
            .fillMaxSize()
            .testTag("puzzle_board_canvas")
            .pointerInput(rows, cols, activeArrows, isPaused) {
                if (isPaused) return@pointerInput
                detectTapGestures { offset ->
                    val pad = size.width * 0.032f
                    val innerW = size.width - pad * 2f
                    val innerH = size.height - pad * 2f
                    val cellW = innerW / cols
                    val cellH = innerH / rows

                    val localX = offset.x - pad
                    val localY = offset.y - pad
                    if (localX < 0f || localY < 0f || localX > innerW || localY > innerH) {
                        return@detectTapGestures
                    }

                    val tappedCol = (localX / cellW).toInt().coerceIn(0, cols - 1)
                    val tappedRow = (localY / cellH).toInt().coerceIn(0, rows - 1)

                    val directHit = activeArrows.firstOrNull {
                        it.row == tappedRow && it.col == tappedCol
                    }
                    if (directHit != null) {
                        onArrowTap(directHit.id)
                    } else {
                        // Generous touch tolerance within 0.62 cell radius of an arrow center
                        val tapCellX = localX / cellW
                        val tapCellY = localY / cellH
                        val nearest = activeArrows.minByOrNull { piece ->
                            val dx = (piece.col + 0.5f) - tapCellX
                            val dy = (piece.row + 0.5f) - tapCellY
                            dx * dx + dy * dy
                        }
                        if (nearest != null) {
                            val dx = (nearest.col + 0.5f) - tapCellX
                            val dy = (nearest.row + 0.5f) - tapCellY
                            if (dx * dx + dy * dy <= 0.38f) {
                                onArrowTap(nearest.id)
                            }
                        }
                    }
                }
            }
    ) {
        val boardW = size.width
        val boardH = size.height
        val outerPad = boardW * 0.032f
        val innerW = boardW - outerPad * 2f
        val innerH = boardH - outerPad * 2f
        val cellW = innerW / cols
        val cellH = innerH / rows
        val cellSize = minOf(cellW, cellH)
        val cellGap = cellSize * 0.11f

        // Outer board frame
        drawRoundRect(
            color = BoardFrameColor,
            topLeft = Offset.Zero,
            size = Size(boardW, boardH),
            cornerRadius = CornerRadius(24.dp.toPx(), 24.dp.toPx())
        )
        drawRoundRect(
            brush = Brush.linearGradient(
                colors = listOf(
                    ElectricCyan.copy(alpha = 0.35f),
                    GridCellBorder,
                    Color(0xFFA855F7).copy(alpha = 0.35f)
                )
            ),
            topLeft = Offset.Zero,
            size = Size(boardW, boardH),
            cornerRadius = CornerRadius(24.dp.toPx(), 24.dp.toPx()),
            style = Stroke(width = 1.6.dp.toPx())
        )

        // Grid cells
        for (r in 0 until rows) {
            for (c in 0 until cols) {
                val left = outerPad + c * cellW + cellGap * 0.5f
                val top = outerPad + r * cellH + cellGap * 0.5f
                val w = cellW - cellGap
                val h = cellH - cellGap
                val radius = CornerRadius(cellSize * 0.22f, cellSize * 0.22f)

                drawRoundRect(
                    color = GridCellColor.copy(alpha = 0.72f),
                    topLeft = Offset(left, top),
                    size = Size(w, h),
                    cornerRadius = radius
                )
                drawRoundRect(
                    color = GridCellBorder.copy(alpha = 0.45f),
                    topLeft = Offset(left, top),
                    size = Size(w, h),
                    cornerRadius = radius,
                    style = Stroke(width = 1.dp.toPx())
                )
            }
        }

        val now = frameNowMs

        // Hint clear exit ray
        val hintedPiece = hintArrowId?.let { id -> activeArrows.firstOrNull { it.id == id } }
        if (hintedPiece != null) {
            val pulse = 0.55f + 0.45f * sin((now % 1200L) / 1200f * (2f * PI.toFloat()))
            val startCenter = Offset(
                outerPad + (hintedPiece.col + 0.5f) * cellW,
                outerPad + (hintedPiece.row + 0.5f) * cellH
            )
            val exitEnd = when (hintedPiece.direction) {
                Direction.UP -> Offset(startCenter.x, outerPad * 0.3f)
                Direction.DOWN -> Offset(startCenter.x, boardH - outerPad * 0.3f)
                Direction.LEFT -> Offset(outerPad * 0.3f, startCenter.y)
                Direction.RIGHT -> Offset(boardW - outerPad * 0.3f, startCenter.y)
            }
            drawLine(
                color = SolarYellow.copy(alpha = 0.45f + 0.35f * pulse),
                start = startCenter,
                end = exitEnd,
                strokeWidth = cellSize * 0.10f,
                cap = StrokeCap.Round,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 10f), -(now % 400L) / 12f)
            )
        }

        // Active arrows
        val blockedActive = blockedFeedback?.takeIf { now - it.timestampMs < 380L }
        for (piece in activeArrows) {
            val baseCx = outerPad + (piece.col + 0.5f) * cellW
            val baseCy = outerPad + (piece.row + 0.5f) * cellH

            var offsetX = 0f
            var offsetY = 0f
            var isBlockedTarget = false
            var isBlockerHighlight = false

            if (blockedActive != null && blockedActive.arrowId == piece.id) {
                isBlockedTarget = true
                val progress = ((now - blockedActive.timestampMs) / 380f).coerceIn(0f, 1f)
                val envelope = (1f - progress)
                val nudge = sin(progress * PI.toFloat() * 3f) * envelope * (cellSize * 0.14f)
                offsetX = piece.direction.dCol * nudge
                offsetY = piece.direction.dRow * nudge
            } else if (blockedActive != null && blockedActive.blockerId == piece.id) {
                isBlockerHighlight = true
            }

            val isHinted = piece.id == hintArrowId
            val hintScale = if (isHinted) {
                1f + 0.08f * sin((now % 900L) / 900f * 2f * PI.toFloat())
            } else {
                1f
            }

            val finalScale = entranceScale.value * hintScale

            if (isHinted) {
                drawCircle(
                    color = SolarYellow.copy(alpha = 0.28f),
                    radius = cellSize * 0.46f,
                    center = Offset(baseCx, baseCy)
                )
                drawCircle(
                    color = SolarYellow,
                    radius = cellSize * 0.44f,
                    center = Offset(baseCx, baseCy),
                    style = Stroke(width = 2.2.dp.toPx())
                )
            }

            if (isBlockedTarget || isBlockerHighlight) {
                val ringColor = if (isBlockedTarget) CoralRed else CoralRed.copy(alpha = 0.55f)
                drawCircle(
                    color = ringColor.copy(alpha = 0.24f),
                    radius = cellSize * 0.44f,
                    center = Offset(baseCx, baseCy)
                )
                drawCircle(
                    color = ringColor,
                    radius = cellSize * 0.42f,
                    center = Offset(baseCx, baseCy),
                    style = Stroke(width = 2.dp.toPx())
                )
            }

            drawStyledArrow(
                center = Offset(baseCx + offsetX, baseCy + offsetY),
                cellSize = cellSize * finalScale,
                direction = piece.direction,
                fillColor = if (isBlockedTarget) CoralRed else piece.color.fill,
                glowColor = if (isBlockedTarget) CoralRed else piece.color.glow,
                highlightColor = if (isBlockedTarget) Color(0xFFFFB3C1) else piece.color.highlight,
                alpha = 1f
            )
        }

        // Escaping arrows flying smoothly outside the board
        for (esc in escapingArrows) {
            val elapsed = (now - esc.startTimeMs).coerceAtLeast(0L)
            val t = (elapsed / 260f).coerceIn(0f, 1f)
            val eased = t * t * (1.8f - 0.8f * t)
            val travelCells = maxOf(rows, cols).toFloat() + 1.4f
            val baseCx = outerPad + (esc.piece.col + 0.5f) * cellW
            val baseCy = outerPad + (esc.piece.row + 0.5f) * cellH
            val cx = baseCx + esc.piece.direction.dCol * eased * travelCells * cellW
            val cy = baseCy + esc.piece.direction.dRow * eased * travelCells * cellH
            val alpha = (1f - t * 0.85f).coerceIn(0f, 1f)

            if (cx in -cellW..boardW + cellW && cy in -cellH..boardH + cellH) {
                drawStyledArrow(
                    center = Offset(cx, cy),
                    cellSize = cellSize * (1f + 0.08f * (1f - t)),
                    direction = esc.piece.direction,
                    fillColor = esc.piece.color.fill,
                    glowColor = esc.piece.color.glow,
                    highlightColor = esc.piece.color.highlight,
                    alpha = alpha
                )
            }
        }

        // Escape burst particles
        for (p in burstParticles) {
            val age = (now - p.bornAtMs).coerceIn(0L, 420L) / 420f
            val px = outerPad + (p.originX + p.vx * age * 1.4f) * cellW
            val py = outerPad + (p.originY + p.vy * age * 1.4f) * cellH
            val alpha = (1f - age).coerceIn(0f, 1f)
            drawCircle(
                color = p.color.copy(alpha = alpha * 0.85f),
                radius = p.radius * (1f - age * 0.45f) * density,
                center = Offset(px, py)
            )
        }
    }
}

private fun DrawScope.drawStyledArrow(
    center: Offset,
    cellSize: Float,
    direction: Direction,
    fillColor: Color,
    glowColor: Color,
    highlightColor: Color,
    alpha: Float
) {
    val angleRad = when (direction) {
        Direction.RIGHT -> 0f
        Direction.DOWN -> (PI / 2.0).toFloat()
        Direction.LEFT -> PI.toFloat()
        Direction.UP -> (-PI / 2.0).toFloat()
    }
    val cosA = cos(angleRad)
    val sinA = sin(angleRad)

    fun pt(lx: Float, ly: Float): Offset {
        val sx = lx * cellSize
        val sy = ly * cellSize
        return Offset(
            center.x + sx * cosA - sy * sinA,
            center.y + sx * sinA + sy * cosA
        )
    }

    // Tile backing subtle glow
    drawCircle(
        color = glowColor.copy(alpha = 0.18f * alpha),
        radius = cellSize * 0.40f,
        center = center
    )

    val tail = pt(-0.30f, 0f)
    val shaftEnd = pt(0.08f, 0f)

    // Outer shaft glow
    drawLine(
        color = glowColor.copy(alpha = 0.34f * alpha),
        start = tail,
        end = shaftEnd,
        strokeWidth = cellSize * 0.24f,
        cap = StrokeCap.Round
    )

    // Main shaft
    drawLine(
        color = fillColor.copy(alpha = alpha),
        start = tail,
        end = shaftEnd,
        strokeWidth = cellSize * 0.155f,
        cap = StrokeCap.Round
    )

    // Arrowhead
    val tip = pt(0.35f, 0f)
    val topWing = pt(0.01f, -0.26f)
    val notch = pt(0.07f, 0f)
    val bottomWing = pt(0.01f, 0.26f)

    val headPath = Path().apply {
        moveTo(tip.x, tip.y)
        lineTo(topWing.x, topWing.y)
        lineTo(notch.x, notch.y)
        lineTo(bottomWing.x, bottomWing.y)
        close()
    }

    // Soft glow around arrowhead
    drawPath(
        path = headPath,
        color = glowColor.copy(alpha = 0.38f * alpha),
        style = Stroke(width = cellSize * 0.09f, join = StrokeJoin.Round)
    )
    drawPath(
        path = headPath,
        color = fillColor.copy(alpha = alpha)
    )
    drawPath(
        path = headPath,
        color = fillColor.copy(alpha = alpha),
        style = Stroke(width = cellSize * 0.045f, join = StrokeJoin.Round)
    )

    // Specular highlight spine along shaft for crisp 3D polish
    drawLine(
        color = highlightColor.copy(alpha = 0.65f * alpha),
        start = pt(-0.24f, -0.025f),
        end = pt(0.18f, -0.025f),
        strokeWidth = cellSize * 0.04f,
        cap = StrokeCap.Round
    )
}
