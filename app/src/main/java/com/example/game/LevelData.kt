package com.example.game

import com.example.model.ArrowColor
import com.example.model.ArrowPiece
import com.example.model.Direction
import com.example.model.LevelDefinition

object LevelData {

    private val palette = ArrowColor.entries

    private fun buildArrows(
        levelOffset: Int,
        specs: List<Triple<Int, Int, Direction>>
    ): List<ArrowPiece> {
        return specs.mapIndexed { index, (row, col, dir) ->
            val colorIndex = (index + levelOffset + row * 2 + col) % palette.size
            ArrowPiece(
                id = levelOffset * 100 + index + 1,
                row = row,
                col = col,
                direction = dir,
                color = palette[colorIndex]
            )
        }
    }

    val levels: List<LevelDefinition> = listOf(
        // Level 1 – Tutorial (4x4, 5 arrows)
        LevelDefinition(
            number = 1,
            title = "First Flight",
            difficultyLabel = "Tutorial",
            rows = 4,
            cols = 4,
            freeHints = 3,
            arrows = buildArrows(
                1,
                listOf(
                    Triple(0, 2, Direction.RIGHT),
                    Triple(0, 3, Direction.DOWN),
                    Triple(1, 1, Direction.RIGHT),
                    Triple(1, 2, Direction.UP),
                    Triple(2, 3, Direction.LEFT)
                )
            )
        ),
        // Level 2 – Beginner (4x4, 8 arrows)
        LevelDefinition(
            number = 2,
            title = "Clear Order",
            difficultyLabel = "Beginner",
            rows = 4,
            cols = 4,
            freeHints = 3,
            arrows = buildArrows(
                2,
                listOf(
                    Triple(0, 0, Direction.RIGHT),
                    Triple(0, 1, Direction.UP),
                    Triple(1, 0, Direction.UP),
                    Triple(1, 1, Direction.LEFT),
                    Triple(2, 0, Direction.DOWN),
                    Triple(2, 2, Direction.LEFT),
                    Triple(3, 0, Direction.RIGHT),
                    Triple(3, 1, Direction.UP)
                )
            )
        ),
        // Level 3 – Beginner+ (5x5, 12 arrows)
        LevelDefinition(
            number = 3,
            title = "Crosswinds",
            difficultyLabel = "Beginner+",
            rows = 5,
            cols = 5,
            freeHints = 3,
            arrows = buildArrows(
                3,
                listOf(
                    Triple(1, 1, Direction.DOWN),
                    Triple(1, 2, Direction.RIGHT),
                    Triple(2, 1, Direction.RIGHT),
                    Triple(2, 3, Direction.DOWN),
                    Triple(3, 1, Direction.RIGHT),
                    Triple(3, 3, Direction.RIGHT),
                    Triple(3, 4, Direction.DOWN),
                    Triple(4, 0, Direction.UP),
                    Triple(4, 1, Direction.LEFT),
                    Triple(4, 2, Direction.UP),
                    Triple(4, 3, Direction.LEFT),
                    Triple(4, 4, Direction.LEFT)
                )
            )
        ),
        // Level 4 – Easy (5x5, 16 arrows)
        LevelDefinition(
            number = 4,
            title = "Chain Gate",
            difficultyLabel = "Easy",
            rows = 5,
            cols = 5,
            freeHints = 3,
            arrows = buildArrows(
                4,
                listOf(
                    Triple(0, 1, Direction.RIGHT),
                    Triple(0, 2, Direction.UP),
                    Triple(1, 0, Direction.LEFT),
                    Triple(1, 1, Direction.LEFT),
                    Triple(1, 2, Direction.RIGHT),
                    Triple(1, 3, Direction.RIGHT),
                    Triple(1, 4, Direction.DOWN),
                    Triple(2, 0, Direction.UP),
                    Triple(2, 1, Direction.UP),
                    Triple(2, 2, Direction.UP),
                    Triple(2, 3, Direction.LEFT),
                    Triple(3, 3, Direction.DOWN),
                    Triple(3, 4, Direction.LEFT),
                    Triple(4, 1, Direction.LEFT),
                    Triple(4, 3, Direction.LEFT),
                    Triple(4, 4, Direction.LEFT)
                )
            )
        ),
        // Level 5 – Normal (6x5, 20 arrows)
        LevelDefinition(
            number = 5,
            title = "Long Corridors",
            difficultyLabel = "Normal",
            rows = 6,
            cols = 5,
            freeHints = 3,
            arrows = buildArrows(
                5,
                listOf(
                    Triple(0, 0, Direction.UP),
                    Triple(0, 1, Direction.LEFT),
                    Triple(0, 3, Direction.RIGHT),
                    Triple(1, 1, Direction.UP),
                    Triple(1, 2, Direction.RIGHT),
                    Triple(1, 3, Direction.UP),
                    Triple(2, 0, Direction.UP),
                    Triple(2, 1, Direction.LEFT),
                    Triple(2, 2, Direction.RIGHT),
                    Triple(2, 3, Direction.UP),
                    Triple(2, 4, Direction.DOWN),
                    Triple(3, 1, Direction.UP),
                    Triple(3, 2, Direction.UP),
                    Triple(3, 3, Direction.LEFT),
                    Triple(4, 1, Direction.DOWN),
                    Triple(4, 2, Direction.DOWN),
                    Triple(4, 4, Direction.LEFT),
                    Triple(5, 1, Direction.RIGHT),
                    Triple(5, 2, Direction.DOWN),
                    Triple(5, 4, Direction.DOWN)
                )
            )
        ),
        // Level 6 – Normal+ (6x6, 24 arrows)
        LevelDefinition(
            number = 6,
            title = "Tight Orbit",
            difficultyLabel = "Normal+",
            rows = 6,
            cols = 6,
            freeHints = 3,
            arrows = buildArrows(
                6,
                listOf(
                    Triple(0, 1, Direction.DOWN),
                    Triple(0, 3, Direction.UP),
                    Triple(0, 4, Direction.LEFT),
                    Triple(1, 0, Direction.DOWN),
                    Triple(1, 1, Direction.LEFT),
                    Triple(1, 3, Direction.LEFT),
                    Triple(1, 4, Direction.RIGHT),
                    Triple(2, 0, Direction.DOWN),
                    Triple(2, 2, Direction.DOWN),
                    Triple(2, 3, Direction.RIGHT),
                    Triple(2, 4, Direction.UP),
                    Triple(3, 0, Direction.DOWN),
                    Triple(3, 2, Direction.RIGHT),
                    Triple(3, 3, Direction.UP),
                    Triple(3, 4, Direction.UP),
                    Triple(4, 2, Direction.RIGHT),
                    Triple(4, 3, Direction.RIGHT),
                    Triple(4, 4, Direction.DOWN),
                    Triple(4, 5, Direction.RIGHT),
                    Triple(5, 0, Direction.RIGHT),
                    Triple(5, 1, Direction.DOWN),
                    Triple(5, 2, Direction.RIGHT),
                    Triple(5, 4, Direction.RIGHT),
                    Triple(5, 5, Direction.DOWN)
                )
            )
        ),
        // Level 7 – Hard (6x6, 28 arrows)
        LevelDefinition(
            number = 7,
            title = "Deep Lock",
            difficultyLabel = "Hard",
            rows = 6,
            cols = 6,
            freeHints = 3,
            arrows = buildArrows(
                7,
                listOf(
                    Triple(0, 0, Direction.UP),
                    Triple(0, 1, Direction.RIGHT),
                    Triple(0, 2, Direction.RIGHT),
                    Triple(0, 3, Direction.UP),
                    Triple(0, 4, Direction.RIGHT),
                    Triple(1, 0, Direction.RIGHT),
                    Triple(1, 1, Direction.RIGHT),
                    Triple(1, 2, Direction.UP),
                    Triple(1, 3, Direction.UP),
                    Triple(1, 5, Direction.DOWN),
                    Triple(2, 0, Direction.UP),
                    Triple(2, 1, Direction.LEFT),
                    Triple(2, 2, Direction.RIGHT),
                    Triple(2, 3, Direction.DOWN),
                    Triple(2, 4, Direction.DOWN),
                    Triple(2, 5, Direction.DOWN),
                    Triple(3, 1, Direction.UP),
                    Triple(3, 3, Direction.RIGHT),
                    Triple(3, 4, Direction.DOWN),
                    Triple(3, 5, Direction.DOWN),
                    Triple(4, 0, Direction.UP),
                    Triple(4, 1, Direction.UP),
                    Triple(4, 2, Direction.LEFT),
                    Triple(4, 3, Direction.LEFT),
                    Triple(4, 4, Direction.RIGHT),
                    Triple(4, 5, Direction.RIGHT),
                    Triple(5, 4, Direction.RIGHT),
                    Triple(5, 5, Direction.RIGHT)
                )
            )
        ),
        // Level 8 – Hard+ (7x6, 32 arrows)
        LevelDefinition(
            number = 8,
            title = "Grid Nexus",
            difficultyLabel = "Hard+",
            rows = 7,
            cols = 6,
            freeHints = 3,
            arrows = buildArrows(
                8,
                listOf(
                    Triple(0, 0, Direction.LEFT),
                    Triple(0, 1, Direction.LEFT),
                    Triple(0, 3, Direction.UP),
                    Triple(0, 4, Direction.LEFT),
                    Triple(0, 5, Direction.UP),
                    Triple(1, 0, Direction.UP),
                    Triple(1, 1, Direction.LEFT),
                    Triple(1, 2, Direction.DOWN),
                    Triple(1, 3, Direction.LEFT),
                    Triple(1, 4, Direction.RIGHT),
                    Triple(1, 5, Direction.UP),
                    Triple(2, 1, Direction.UP),
                    Triple(2, 2, Direction.LEFT),
                    Triple(2, 3, Direction.RIGHT),
                    Triple(2, 4, Direction.UP),
                    Triple(2, 5, Direction.RIGHT),
                    Triple(3, 0, Direction.DOWN),
                    Triple(3, 1, Direction.LEFT),
                    Triple(3, 2, Direction.LEFT),
                    Triple(3, 3, Direction.UP),
                    Triple(4, 0, Direction.DOWN),
                    Triple(4, 1, Direction.DOWN),
                    Triple(4, 2, Direction.LEFT),
                    Triple(5, 0, Direction.DOWN),
                    Triple(5, 2, Direction.RIGHT),
                    Triple(5, 5, Direction.RIGHT),
                    Triple(6, 0, Direction.RIGHT),
                    Triple(6, 1, Direction.DOWN),
                    Triple(6, 2, Direction.RIGHT),
                    Triple(6, 3, Direction.RIGHT),
                    Triple(6, 4, Direction.UP),
                    Triple(6, 5, Direction.UP)
                )
            )
        ),
        // Level 9 – Expert (7x7, 36 arrows)
        LevelDefinition(
            number = 9,
            title = "Prism Vault",
            difficultyLabel = "Expert",
            rows = 7,
            cols = 7,
            freeHints = 3,
            arrows = buildArrows(
                9,
                listOf(
                    Triple(0, 0, Direction.DOWN),
                    Triple(0, 1, Direction.DOWN),
                    Triple(0, 2, Direction.UP),
                    Triple(0, 4, Direction.UP),
                    Triple(0, 5, Direction.LEFT),
                    Triple(0, 6, Direction.LEFT),
                    Triple(1, 0, Direction.DOWN),
                    Triple(1, 1, Direction.DOWN),
                    Triple(1, 2, Direction.LEFT),
                    Triple(1, 6, Direction.LEFT),
                    Triple(2, 0, Direction.DOWN),
                    Triple(2, 1, Direction.DOWN),
                    Triple(2, 2, Direction.LEFT),
                    Triple(2, 5, Direction.LEFT),
                    Triple(2, 6, Direction.UP),
                    Triple(3, 0, Direction.DOWN),
                    Triple(3, 1, Direction.LEFT),
                    Triple(3, 3, Direction.RIGHT),
                    Triple(3, 4, Direction.DOWN),
                    Triple(3, 5, Direction.UP),
                    Triple(3, 6, Direction.UP),
                    Triple(4, 0, Direction.LEFT),
                    Triple(4, 1, Direction.DOWN),
                    Triple(4, 2, Direction.UP),
                    Triple(4, 3, Direction.DOWN),
                    Triple(4, 4, Direction.LEFT),
                    Triple(5, 0, Direction.DOWN),
                    Triple(5, 3, Direction.RIGHT),
                    Triple(5, 4, Direction.RIGHT),
                    Triple(5, 5, Direction.UP),
                    Triple(5, 6, Direction.UP),
                    Triple(6, 0, Direction.LEFT),
                    Triple(6, 1, Direction.RIGHT),
                    Triple(6, 4, Direction.RIGHT),
                    Triple(6, 5, Direction.DOWN),
                    Triple(6, 6, Direction.DOWN)
                )
            )
        ),
        // Level 10 – Final Challenge (7x7, 42 arrows)
        LevelDefinition(
            number = 10,
            title = "Omega Singularity",
            difficultyLabel = "Final Challenge",
            rows = 7,
            cols = 7,
            freeHints = 3,
            arrows = buildArrows(
                10,
                listOf(
                    Triple(0, 0, Direction.LEFT),
                    Triple(0, 1, Direction.RIGHT),
                    Triple(0, 2, Direction.RIGHT),
                    Triple(0, 3, Direction.UP),
                    Triple(0, 4, Direction.RIGHT),
                    Triple(0, 5, Direction.RIGHT),
                    Triple(0, 6, Direction.RIGHT),
                    Triple(1, 0, Direction.LEFT),
                    Triple(1, 1, Direction.LEFT),
                    Triple(1, 2, Direction.RIGHT),
                    Triple(1, 4, Direction.RIGHT),
                    Triple(1, 5, Direction.RIGHT),
                    Triple(1, 6, Direction.RIGHT),
                    Triple(2, 0, Direction.LEFT),
                    Triple(2, 1, Direction.LEFT),
                    Triple(2, 2, Direction.RIGHT),
                    Triple(2, 5, Direction.RIGHT),
                    Triple(3, 0, Direction.UP),
                    Triple(3, 1, Direction.UP),
                    Triple(3, 2, Direction.LEFT),
                    Triple(3, 3, Direction.RIGHT),
                    Triple(3, 4, Direction.RIGHT),
                    Triple(3, 5, Direction.DOWN),
                    Triple(3, 6, Direction.DOWN),
                    Triple(4, 0, Direction.LEFT),
                    Triple(4, 1, Direction.LEFT),
                    Triple(4, 2, Direction.UP),
                    Triple(4, 3, Direction.DOWN),
                    Triple(4, 4, Direction.DOWN),
                    Triple(4, 5, Direction.LEFT),
                    Triple(5, 0, Direction.LEFT),
                    Triple(5, 3, Direction.RIGHT),
                    Triple(5, 4, Direction.DOWN),
                    Triple(5, 5, Direction.DOWN),
                    Triple(5, 6, Direction.DOWN),
                    Triple(6, 0, Direction.UP),
                    Triple(6, 1, Direction.UP),
                    Triple(6, 2, Direction.UP),
                    Triple(6, 3, Direction.LEFT),
                    Triple(6, 4, Direction.LEFT),
                    Triple(6, 5, Direction.DOWN),
                    Triple(6, 6, Direction.DOWN)
                )
            )
        )
    )

    fun getLevel(number: Int): LevelDefinition {
        return levels.firstOrNull { it.number == number } ?: levels.first()
    }
}
