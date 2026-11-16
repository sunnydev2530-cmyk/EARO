package com.example

import com.example.game.LevelData
import com.example.game.PuzzleEngine
import com.example.model.ArrowColor
import com.example.model.ArrowPiece
import com.example.model.Direction
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class PuzzleEngineAndLevelsTest {

    @Test
    fun `all 10 levels exist with expected arrow counts and unique coordinates`() {
        assertEquals(10, LevelData.levels.size)
        val expectedCounts = listOf(5, 8, 12, 16, 20, 24, 28, 32, 36, 42)

        LevelData.levels.forEachIndexed { index, level ->
            assertEquals(index + 1, level.number)
            assertEquals("Level ${level.number} arrow count mismatch", expectedCounts[index], level.arrows.size)

            val coords = level.arrows.map { it.row to it.col }.toSet()
            assertEquals("Duplicate coordinates in level ${level.number}", level.arrows.size, coords.size)

            val ids = level.arrows.map { it.id }.toSet()
            assertEquals("Duplicate IDs in level ${level.number}", level.arrows.size, ids.size)

            level.arrows.forEach { arrow ->
                assertTrue(arrow.row in 0 until level.rows)
                assertTrue(arrow.col in 0 until level.cols)
            }
        }
    }

    @Test
    fun `all 10 levels are mathematically solvable and hint solver clears every board`() {
        for (level in LevelData.levels) {
            assertTrue(
                "Level ${level.number} (${level.title}) failed topological solvability",
                PuzzleEngine.isBoardSolvable(level.arrows, level.rows, level.cols)
            )

            // Simulate solving the entire level step-by-step using findHintArrow
            val remaining = level.arrows.toMutableList()
            var steps = 0
            while (remaining.isNotEmpty() && steps <= level.arrows.size) {
                val hint = PuzzleEngine.findHintArrow(remaining, level.rows, level.cols)
                assertNotNull("Hint returned null while arrows remain in level ${level.number}", hint)
                assertTrue(
                    "Hint returned a blocked arrow in level ${level.number}",
                    PuzzleEngine.canArrowEscape(hint!!, remaining, level.rows, level.cols)
                )
                remaining.remove(hint)
                steps++
            }
            assertTrue("Level ${level.number} did not clear completely", remaining.isEmpty())
            assertEquals(level.arrows.size, steps)
        }
    }

    @Test
    fun `collision detection checks full ray in all four directions`() {
        val blocker = ArrowPiece(1, 1, 2, Direction.UP, ArrowColor.CYAN)
        val blockedRight = ArrowPiece(2, 1, 0, Direction.RIGHT, ArrowColor.PINK)
        val blockedDown = ArrowPiece(3, 0, 2, Direction.DOWN, ArrowColor.ORANGE)
        val clearLeft = ArrowPiece(4, 2, 1, Direction.LEFT, ArrowColor.GREEN)
        val clearDown = ArrowPiece(5, 3, 2, Direction.DOWN, ArrowColor.YELLOW)

        val board = listOf(blocker, blockedRight, blockedDown, clearLeft, clearDown)

        assertFalse(PuzzleEngine.canArrowEscape(blockedRight, board, 4, 4))
        assertEquals(blocker.id, PuzzleEngine.findBlockingArrow(blockedRight, board, 4, 4)?.id)

        assertFalse(PuzzleEngine.canArrowEscape(blockedDown, board, 4, 4))
        assertEquals(blocker.id, PuzzleEngine.findBlockingArrow(blockedDown, board, 4, 4)?.id)

        assertTrue(PuzzleEngine.canArrowEscape(clearLeft, board, 4, 4))
        assertNull(PuzzleEngine.findBlockingArrow(clearLeft, board, 4, 4))

        assertTrue(PuzzleEngine.canArrowEscape(clearDown, board, 4, 4))
        assertNull(PuzzleEngine.findBlockingArrow(clearDown, board, 4, 4))
    }

    @Test
    fun `star rating rewards clean solves`() {
        assertEquals(3, PuzzleEngine.calculateStars(blockedAttempts = 0, hintsUsed = 0))
        assertEquals(2, PuzzleEngine.calculateStars(blockedAttempts = 1, hintsUsed = 0))
        assertEquals(2, PuzzleEngine.calculateStars(blockedAttempts = 2, hintsUsed = 1))
        assertEquals(1, PuzzleEngine.calculateStars(blockedAttempts = 3, hintsUsed = 0))
        assertEquals(1, PuzzleEngine.calculateStars(blockedAttempts = 0, hintsUsed = 2))
    }
}
