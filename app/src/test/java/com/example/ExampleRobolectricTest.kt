package com.example

import android.app.Application
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.ProgressRepository
import com.example.game.GameViewModel
import com.example.game.PuzzleEngine
import com.example.model.GameSettings
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read app name from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Arrow Escape Puzzle", appName)
    }

    @Test
    fun `progress repository persists unlocks stars and settings across restarts`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val repo1 = ProgressRepository(context)
        repo1.resetProgress(10)

        val initial = repo1.loadAllLevelProgress(10)
        assertTrue(initial[0].unlocked)
        assertFalse(initial[1].unlocked)

        repo1.recordLevelCompletion(levelNumber = 1, starsEarned = 3, movesUsed = 5, totalLevels = 10)
        repo1.saveSettings(
            GameSettings(
                sfxEnabled = false,
                musicEnabled = false,
                sfxVolume = 0.4f,
                musicVolume = 0.2f,
                vibrationEnabled = false
            )
        )

        // Simulate app restart with fresh repository instance
        val repo2 = ProgressRepository(context)
        val reloaded = repo2.loadAllLevelProgress(10)
        assertTrue(reloaded[0].completed)
        assertEquals(3, reloaded[0].bestStars)
        assertEquals(5, reloaded[0].bestMoves)
        assertTrue("Level 2 should remain unlocked", reloaded[1].unlocked)

        val settings = repo2.loadSettings()
        assertFalse(settings.sfxEnabled)
        assertFalse(settings.musicEnabled)
        assertEquals(0.4f, settings.sfxVolume, 0.01f)
        assertFalse(settings.vibrationEnabled)
    }

    @Test
    fun `gameplay flow handles blocked arrows, hints, undo, and restart`() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        ProgressRepository(app).resetProgress(10)

        val vm = GameViewModel(app)
        vm.startLevel(1)

        val startState = vm.uiState.value.gameplay
        assertEquals(5, startState.remainingCount)

        // Find a blocked arrow on Level 1 and verify tapping it does not remove it
        val blockedArrow = startState.activeArrows.first { arrow ->
            !PuzzleEngine.canArrowEscape(arrow, startState.activeArrows, startState.level.rows, startState.level.cols)
        }
        vm.onArrowTapped(blockedArrow.id)

        val afterBlocked = vm.uiState.value.gameplay
        assertEquals(5, afterBlocked.remainingCount)
        assertEquals(1, afterBlocked.blockedAttemptsCount)
        assertNotNull(afterBlocked.blockedFeedback)

        // Use hint and tap the hinted arrow (wait >110ms for tap debounce)
        Thread.sleep(130L)
        vm.useHint()
        val hintId = vm.uiState.value.gameplay.hintArrowId
        assertNotNull(hintId)
        assertEquals(2, vm.uiState.value.gameplay.hintsRemaining)

        vm.onArrowTapped(hintId!!)
        val afterEscape = vm.uiState.value.gameplay
        assertEquals(4, afterEscape.remainingCount)
        assertEquals(1, afterEscape.validMovesCount)

        // Undo restores the exact arrow
        vm.undoLastMove()
        val afterUndo = vm.uiState.value.gameplay
        assertEquals(5, afterUndo.remainingCount)
        assertTrue(afterUndo.activeArrows.any { it.id == hintId })

        // Restart restores clean initial board
        vm.confirmRestart()
        val afterRestart = vm.uiState.value.gameplay
        assertEquals(5, afterRestart.remainingCount)
        assertEquals(0, afterRestart.validMovesCount)
        assertEquals(0, afterRestart.blockedAttemptsCount)
    }
}
