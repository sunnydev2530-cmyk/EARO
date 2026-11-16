package com.example.model

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.HotPink
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.SolarYellow
import com.example.ui.theme.VividPurple
import com.example.ui.theme.WarmOrange

enum class Direction(val dRow: Int, val dCol: Int) {
    UP(-1, 0),
    DOWN(1, 0),
    LEFT(0, -1),
    RIGHT(0, 1)
}

enum class ArrowColor(
    val fill: Color,
    val glow: Color,
    val highlight: Color
) {
    CYAN(
        fill = ElectricCyan,
        glow = Color(0xFF00D4FF),
        highlight = Color(0xFFA5FBFF)
    ),
    ELECTRIC_BLUE(
        fill = ElectricBlue,
        glow = Color(0xFF60A5FA),
        highlight = Color(0xFFBFDBFE)
    ),
    PURPLE(
        fill = VividPurple,
        glow = Color(0xFFC084FC),
        highlight = Color(0xFFE9D5FF)
    ),
    ORANGE(
        fill = WarmOrange,
        glow = Color(0xFFFB923C),
        highlight = Color(0xFFFED7AA)
    ),
    PINK(
        fill = HotPink,
        glow = Color(0xFFF472B6),
        highlight = Color(0xFFFBCFE8)
    ),
    GREEN(
        fill = NeonGreen,
        glow = Color(0xFF4ADE80),
        highlight = Color(0xFFBBF7D0)
    ),
    YELLOW(
        fill = SolarYellow,
        glow = Color(0xFFFACC15),
        highlight = Color(0xFFFEF08A)
    )
}

data class ArrowPiece(
    val id: Int,
    val row: Int,
    val col: Int,
    val direction: Direction,
    val color: ArrowColor
)

data class LevelDefinition(
    val number: Int,
    val title: String,
    val difficultyLabel: String,
    val rows: Int,
    val cols: Int,
    val arrows: List<ArrowPiece>,
    val freeHints: Int = 3
)

data class LevelProgress(
    val levelNumber: Int,
    val unlocked: Boolean,
    val completed: Boolean,
    val bestStars: Int,
    val bestMoves: Int? = null
)

data class GameSettings(
    val sfxEnabled: Boolean = true,
    val musicEnabled: Boolean = true,
    val sfxVolume: Float = 0.85f,
    val musicVolume: Float = 0.35f,
    val vibrationEnabled: Boolean = true
)

enum class AppScreen {
    SPLASH,
    MAIN_MENU,
    LEVEL_SELECT,
    GAMEPLAY,
    SETTINGS,
    HOW_TO_PLAY,
    PRIVACY_POLICY
}
