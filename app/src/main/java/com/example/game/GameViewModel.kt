package com.example.game

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.SoundEffect
import com.example.audio.SoundEngine
import com.example.data.ProgressRepository
import com.example.haptic.HapticController
import com.example.model.AppScreen
import com.example.model.ArrowPiece
import com.example.model.GameSettings
import com.example.model.LevelDefinition
import com.example.model.LevelProgress
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class EscapingArrowState(
    val piece: ArrowPiece,
    val startTimeMs: Long
)

data class BlockedFeedback(
    val arrowId: Int,
    val blockerId: Int?,
    val timestampMs: Long
)

data class GameplayUiState(
    val level: LevelDefinition = LevelData.getLevel(1),
    val activeArrows: List<ArrowPiece> = emptyList(),
    val escapingArrows: List<EscapingArrowState> = emptyList(),
    val undoStack: List<ArrowPiece> = emptyList(),
    val blockedFeedback: BlockedFeedback? = null,
    val hintArrowId: Int? = null,
    val hintsRemaining: Int = 3,
    val hintsUsed: Int = 0,
    val validMovesCount: Int = 0,
    val blockedAttemptsCount: Int = 0,
    val isPaused: Boolean = false,
    val isCompleted: Boolean = false,
    val earnedStars: Int = 0,
    val newlyUnlockedNextLevel: Boolean = false,
    val showRestartConfirm: Boolean = false,
    val boardEntranceNonce: Int = 0
) {
    val totalArrows: Int get() = level.arrows.size
    val remainingCount: Int get() = activeArrows.size
    val clearedCount: Int get() = (totalArrows - activeArrows.size).coerceAtLeast(0)
    val progressFraction: Float
        get() = if (totalArrows > 0) clearedCount.toFloat() / totalArrows.toFloat() else 0f
}

data class AppUiState(
    val currentScreen: AppScreen = AppScreen.SPLASH,
    val settings: GameSettings = GameSettings(),
    val levelProgress: List<LevelProgress> = emptyList(),
    val gameplay: GameplayUiState = GameplayUiState(),
    val showResetProgressDialog: Boolean = false,
    val isForeground: Boolean = true
) {
    val highestUnlockedLevel: Int
        get() = levelProgress.lastOrNull { it.unlocked }?.levelNumber ?: 1

    val completedLevelsCount: Int
        get() = levelProgress.count { it.completed }

    val totalStarsEarned: Int
        get() = levelProgress.sumOf { it.bestStars }

    val maxStarsPossible: Int
        get() = levelProgress.size * 3
}

class GameViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ProgressRepository(application)
    val soundEngine = SoundEngine()
    private val haptics = HapticController(application)

    private val _uiState = MutableStateFlow(AppUiState())
    val uiState: StateFlow<AppUiState> = _uiState.asStateFlow()

    private var lastTapTimestampMs: Long = 0L

    init {
        val loadedSettings = repository.loadSettings()
        val loadedProgress = repository.loadAllLevelProgress(LevelData.levels.size)
        _uiState.update {
            it.copy(
                settings = loadedSettings,
                levelProgress = loadedProgress
            )
        }
        soundEngine.updateSettings(
            sfxEnabled = loadedSettings.sfxEnabled,
            musicEnabled = loadedSettings.musicEnabled,
            sfxVolume = loadedSettings.sfxVolume,
            musicVolume = loadedSettings.musicVolume
        )
        soundEngine.initialize()

        viewModelScope.launch {
            delay(1150L)
            _uiState.update { state ->
                if (state.currentScreen == AppScreen.SPLASH) {
                    state.copy(currentScreen = AppScreen.MAIN_MENU)
                } else {
                    state
                }
            }
        }
    }

    fun onAppForegroundChanged(isForeground: Boolean) {
        _uiState.update { it.copy(isForeground = isForeground) }
        soundEngine.onForegroundChanged(isForeground)
    }

    fun playButtonClick() {
        soundEngine.play(SoundEffect.BUTTON_CLICK)
        haptics.lightTap(_uiState.value.settings.vibrationEnabled)
    }

    fun navigateTo(screen: AppScreen) {
        playButtonClick()
        _uiState.update { it.copy(currentScreen = screen) }
    }

    fun playContinue() {
        val highest = _uiState.value.highestUnlockedLevel
        val firstIncomplete = _uiState.value.levelProgress.firstOrNull { it.unlocked && !it.completed }?.levelNumber
        startLevel(firstIncomplete ?: highest)
    }

    fun startLevel(levelNumber: Int) {
        val progress = _uiState.value.levelProgress.firstOrNull { it.levelNumber == levelNumber }
        if (progress != null && !progress.unlocked) {
            soundEngine.play(SoundEffect.BLOCKED_ARROW)
            return
        }
        playButtonClick()
        val level = LevelData.getLevel(levelNumber)
        _uiState.update { state ->
            state.copy(
                currentScreen = AppScreen.GAMEPLAY,
                gameplay = GameplayUiState(
                    level = level,
                    activeArrows = level.arrows,
                    escapingArrows = emptyList(),
                    undoStack = emptyList(),
                    blockedFeedback = null,
                    hintArrowId = null,
                    hintsRemaining = level.freeHints,
                    hintsUsed = 0,
                    validMovesCount = 0,
                    blockedAttemptsCount = 0,
                    isPaused = false,
                    isCompleted = false,
                    earnedStars = 0,
                    newlyUnlockedNextLevel = false,
                    showRestartConfirm = false,
                    boardEntranceNonce = state.gameplay.boardEntranceNonce + 1
                )
            )
        }
    }

    fun onArrowTapped(arrowId: Int) {
        val now = System.currentTimeMillis()
        if (now - lastTapTimestampMs < 110L) return
        lastTapTimestampMs = now

        val state = _uiState.value
        val gp = state.gameplay
        if (state.currentScreen != AppScreen.GAMEPLAY || gp.isPaused || gp.isCompleted) return

        val tappedArrow = gp.activeArrows.firstOrNull { it.id == arrowId } ?: return
        soundEngine.play(SoundEffect.ARROW_SELECT)

        // Check both active grid arrows and any currently flying escaping arrows
        val combinedObstacles = gp.activeArrows + gp.escapingArrows.map { it.piece }
        val blocker = PuzzleEngine.findBlockingArrow(
            arrow = tappedArrow,
            activeArrows = combinedObstacles,
            rows = gp.level.rows,
            cols = gp.level.cols
        )

        if (blocker != null) {
            soundEngine.play(SoundEffect.BLOCKED_ARROW)
            haptics.blockedBuzz(state.settings.vibrationEnabled)
            _uiState.update { current ->
                current.copy(
                    gameplay = current.gameplay.copy(
                        blockedFeedback = BlockedFeedback(
                            arrowId = tappedArrow.id,
                            blockerId = blocker.id,
                            timestampMs = now
                        ),
                        blockedAttemptsCount = current.gameplay.blockedAttemptsCount + 1
                    )
                )
            }
            return
        }

        // Valid escape!
        val updatedActive = gp.activeArrows.filterNot { it.id == tappedArrow.id }
        val updatedEscaping = gp.escapingArrows + EscapingArrowState(tappedArrow, now)
        val updatedUndo = gp.undoStack + tappedArrow
        val newMoves = gp.validMovesCount + 1

        soundEngine.play(SoundEffect.ARROW_ESCAPE)
        haptics.escapePulse(state.settings.vibrationEnabled)

        val finishedLevel = updatedActive.isEmpty()

        _uiState.update { current ->
            current.copy(
                gameplay = current.gameplay.copy(
                    activeArrows = updatedActive,
                    escapingArrows = updatedEscaping,
                    undoStack = updatedUndo,
                    blockedFeedback = null,
                    hintArrowId = if (current.gameplay.hintArrowId == tappedArrow.id) null else current.gameplay.hintArrowId,
                    validMovesCount = newMoves
                )
            )
        }

        // Clean up finished escape animation after 280ms
        viewModelScope.launch {
            delay(280L)
            _uiState.update { current ->
                current.copy(
                    gameplay = current.gameplay.copy(
                        escapingArrows = current.gameplay.escapingArrows.filterNot { it.piece.id == tappedArrow.id && it.startTimeMs == now }
                    )
                )
            }
        }

        if (finishedLevel) {
            viewModelScope.launch {
                delay(320L)
                completeCurrentLevel()
            }
        }
    }

    private fun completeCurrentLevel() {
        val state = _uiState.value
        val gp = state.gameplay
        if (gp.isCompleted) return

        val stars = PuzzleEngine.calculateStars(gp.blockedAttemptsCount, gp.hintsUsed)
        val totalMoves = gp.validMovesCount + gp.blockedAttemptsCount
        val totalLevels = LevelData.levels.size
        val unlockedNext = repository.isNextLevelNewlyUnlocked(gp.level.number, totalLevels)

        repository.recordLevelCompletion(
            levelNumber = gp.level.number,
            starsEarned = stars,
            movesUsed = totalMoves,
            totalLevels = totalLevels
        )
        val refreshedProgress = repository.loadAllLevelProgress(totalLevels)

        soundEngine.play(SoundEffect.LEVEL_COMPLETE)
        if (unlockedNext) {
            viewModelScope.launch {
                delay(240L)
                soundEngine.play(SoundEffect.LEVEL_UNLOCKED)
            }
        } else if (gp.level.number == totalLevels) {
            viewModelScope.launch {
                delay(220L)
                soundEngine.play(SoundEffect.VICTORY)
            }
        }
        haptics.victoryPulse(state.settings.vibrationEnabled)

        _uiState.update { current ->
            current.copy(
                levelProgress = refreshedProgress,
                gameplay = current.gameplay.copy(
                    isCompleted = true,
                    earnedStars = stars,
                    newlyUnlockedNextLevel = unlockedNext
                )
            )
        }
    }

    fun undoLastMove() {
        val state = _uiState.value
        val gp = state.gameplay
        if (gp.isPaused || gp.isCompleted || gp.undoStack.isEmpty()) return

        val lastRemoved = gp.undoStack.last()
        soundEngine.play(SoundEffect.UNDO)
        haptics.lightTap(state.settings.vibrationEnabled)

        _uiState.update { current ->
            val currentGp = current.gameplay
            if (currentGp.undoStack.isEmpty()) return@update current
            val restored = currentGp.undoStack.last()
            val restoredActive = if (currentGp.activeArrows.any { it.id == restored.id }) {
                currentGp.activeArrows
            } else {
                currentGp.activeArrows + restored
            }
            current.copy(
                gameplay = currentGp.copy(
                    activeArrows = restoredActive,
                    escapingArrows = currentGp.escapingArrows.filterNot { it.piece.id == lastRemoved.id },
                    undoStack = currentGp.undoStack.dropLast(1),
                    blockedFeedback = null,
                    hintArrowId = null
                )
            )
        }
    }

    fun useHint() {
        val state = _uiState.value
        val gp = state.gameplay
        if (gp.isPaused || gp.isCompleted || gp.hintsRemaining <= 0) {
            soundEngine.play(SoundEffect.BLOCKED_ARROW)
            return
        }

        val hintPiece = PuzzleEngine.findHintArrow(
            activeArrows = gp.activeArrows,
            rows = gp.level.rows,
            cols = gp.level.cols
        ) ?: return

        soundEngine.play(SoundEffect.HINT)
        haptics.lightTap(state.settings.vibrationEnabled)

        _uiState.update { current ->
            current.copy(
                gameplay = current.gameplay.copy(
                    hintArrowId = hintPiece.id,
                    hintsRemaining = (current.gameplay.hintsRemaining - 1).coerceAtLeast(0),
                    hintsUsed = current.gameplay.hintsUsed + 1
                )
            )
        }
    }

    fun setPaused(paused: Boolean) {
        playButtonClick()
        _uiState.update { current ->
            current.copy(
                gameplay = current.gameplay.copy(
                    isPaused = paused,
                    showRestartConfirm = false
                )
            )
        }
    }

    fun requestRestart() {
        playButtonClick()
        val gp = _uiState.value.gameplay
        if (gp.validMovesCount == 0 && gp.blockedAttemptsCount == 0 && !gp.isCompleted) {
            confirmRestart()
        } else {
            _uiState.update { current ->
                current.copy(gameplay = current.gameplay.copy(showRestartConfirm = true))
            }
        }
    }

    fun dismissRestartConfirm() {
        playButtonClick()
        _uiState.update { current ->
            current.copy(gameplay = current.gameplay.copy(showRestartConfirm = false))
        }
    }

    fun confirmRestart() {
        val currentLevel = _uiState.value.gameplay.level
        startLevel(currentLevel.number)
    }

    fun playNextLevel() {
        val nextNum = _uiState.value.gameplay.level.number + 1
        if (nextNum <= LevelData.levels.size) {
            startLevel(nextNum)
        } else {
            navigateTo(AppScreen.LEVEL_SELECT)
        }
    }

    fun toggleSfx() {
        val updated = _uiState.value.settings.let { it.copy(sfxEnabled = !it.sfxEnabled) }
        applySettings(updated)
        if (updated.sfxEnabled) soundEngine.play(SoundEffect.BUTTON_CLICK)
    }

    fun toggleMusic() {
        val updated = _uiState.value.settings.let { it.copy(musicEnabled = !it.musicEnabled) }
        applySettings(updated)
        soundEngine.play(SoundEffect.BUTTON_CLICK)
    }

    fun setSfxVolume(volume: Float) {
        val updated = _uiState.value.settings.copy(sfxVolume = volume.coerceIn(0f, 1f))
        applySettings(updated)
    }

    fun setMusicVolume(volume: Float) {
        val updated = _uiState.value.settings.copy(musicVolume = volume.coerceIn(0f, 1f))
        applySettings(updated)
    }

    fun toggleVibration() {
        val updated = _uiState.value.settings.let { it.copy(vibrationEnabled = !it.vibrationEnabled) }
        applySettings(updated)
        soundEngine.play(SoundEffect.BUTTON_CLICK)
        if (updated.vibrationEnabled) haptics.lightTap(true)
    }

    private fun applySettings(newSettings: GameSettings) {
        repository.saveSettings(newSettings)
        soundEngine.updateSettings(
            sfxEnabled = newSettings.sfxEnabled,
            musicEnabled = newSettings.musicEnabled,
            sfxVolume = newSettings.sfxVolume,
            musicVolume = newSettings.musicVolume
        )
        _uiState.update { it.copy(settings = newSettings) }
    }

    fun requestResetProgress() {
        playButtonClick()
        _uiState.update { it.copy(showResetProgressDialog = true) }
    }

    fun dismissResetProgress() {
        playButtonClick()
        _uiState.update { it.copy(showResetProgressDialog = false) }
    }

    fun confirmResetProgress() {
        playButtonClick()
        repository.resetProgress(LevelData.levels.size)
        val refreshed = repository.loadAllLevelProgress(LevelData.levels.size)
        _uiState.update {
            it.copy(
                levelProgress = refreshed,
                showResetProgressDialog = false
            )
        }
    }

    fun handleBackPress(): Boolean {
        val state = _uiState.value
        return when (state.currentScreen) {
            AppScreen.SPLASH -> true
            AppScreen.MAIN_MENU -> false
            AppScreen.GAMEPLAY -> {
                when {
                    state.gameplay.showRestartConfirm -> {
                        dismissRestartConfirm()
                        true
                    }
                    state.gameplay.isCompleted -> {
                        navigateTo(AppScreen.LEVEL_SELECT)
                        true
                    }
                    else -> {
                        setPaused(!state.gameplay.isPaused)
                        true
                    }
                }
            }
            AppScreen.PRIVACY_POLICY -> {
                navigateTo(AppScreen.SETTINGS)
                true
            }
            AppScreen.LEVEL_SELECT,
            AppScreen.SETTINGS,
            AppScreen.HOW_TO_PLAY -> {
                navigateTo(AppScreen.MAIN_MENU)
                true
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        soundEngine.release()
    }
}
