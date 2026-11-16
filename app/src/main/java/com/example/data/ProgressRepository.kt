package com.example.data

import android.content.Context
import com.example.model.GameSettings
import com.example.model.LevelProgress

class ProgressRepository(context: Context) {

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun loadAllLevelProgress(totalLevels: Int = 10): List<LevelProgress> {
        return (1..totalLevels).map { level ->
            val unlocked = if (level == 1) true else safeGetBoolean(keyUnlocked(level), false)
            val completed = safeGetBoolean(keyCompleted(level), false)
            val bestStars = safeGetInt(keyStars(level), 0).coerceIn(0, 3)
            val bestMovesRaw = safeGetInt(keyMoves(level), -1)
            LevelProgress(
                levelNumber = level,
                unlocked = unlocked,
                completed = completed,
                bestStars = bestStars,
                bestMoves = if (bestMovesRaw > 0) bestMovesRaw else null
            )
        }
    }

    fun recordLevelCompletion(
        levelNumber: Int,
        starsEarned: Int,
        movesUsed: Int,
        totalLevels: Int = 10
    ) {
        val prevCompleted = safeGetBoolean(keyCompleted(levelNumber), false)
        val prevStars = safeGetInt(keyStars(levelNumber), 0).coerceIn(0, 3)
        val prevMoves = safeGetInt(keyMoves(levelNumber), Int.MAX_VALUE)

        val editor = prefs.edit()
            .putBoolean(keyUnlocked(levelNumber), true)
            .putBoolean(keyCompleted(levelNumber), true)
            .putInt(keyStars(levelNumber), maxOf(prevStars, starsEarned.coerceIn(1, 3)))

        if (!prevCompleted || movesUsed < prevMoves) {
            editor.putInt(keyMoves(levelNumber), movesUsed)
        }

        if (levelNumber < totalLevels) {
            editor.putBoolean(keyUnlocked(levelNumber + 1), true)
        }

        editor.apply()
    }

    fun isNextLevelNewlyUnlocked(levelNumber: Int, totalLevels: Int = 10): Boolean {
        if (levelNumber >= totalLevels) return false
        return !safeGetBoolean(keyUnlocked(levelNumber + 1), false)
    }

    fun loadSettings(): GameSettings {
        return GameSettings(
            sfxEnabled = safeGetBoolean(KEY_SFX_ENABLED, true),
            musicEnabled = safeGetBoolean(KEY_MUSIC_ENABLED, true),
            sfxVolume = safeGetFloat(KEY_SFX_VOLUME, 0.85f).coerceIn(0f, 1f),
            musicVolume = safeGetFloat(KEY_MUSIC_VOLUME, 0.35f).coerceIn(0f, 1f),
            vibrationEnabled = safeGetBoolean(KEY_VIBRATION_ENABLED, true)
        )
    }

    fun saveSettings(settings: GameSettings) {
        prefs.edit()
            .putBoolean(KEY_SFX_ENABLED, settings.sfxEnabled)
            .putBoolean(KEY_MUSIC_ENABLED, settings.musicEnabled)
            .putFloat(KEY_SFX_VOLUME, settings.sfxVolume.coerceIn(0f, 1f))
            .putFloat(KEY_MUSIC_VOLUME, settings.musicVolume.coerceIn(0f, 1f))
            .putBoolean(KEY_VIBRATION_ENABLED, settings.vibrationEnabled)
            .apply()
    }

    fun resetProgress(totalLevels: Int = 10) {
        val editor = prefs.edit()
        for (level in 1..totalLevels) {
            editor.remove(keyUnlocked(level))
            editor.remove(keyCompleted(level))
            editor.remove(keyStars(level))
            editor.remove(keyMoves(level))
        }
        editor.putBoolean(keyUnlocked(1), true)
        editor.apply()
    }

    private fun safeGetBoolean(key: String, default: Boolean): Boolean =
        runCatching { prefs.getBoolean(key, default) }.getOrDefault(default)

    private fun safeGetInt(key: String, default: Int): Int =
        runCatching { prefs.getInt(key, default) }.getOrDefault(default)

    private fun safeGetFloat(key: String, default: Float): Float =
        runCatching { prefs.getFloat(key, default) }.getOrDefault(default)

    private fun keyUnlocked(level: Int) = "level_${level}_unlocked"
    private fun keyCompleted(level: Int) = "level_${level}_completed"
    private fun keyStars(level: Int) = "level_${level}_stars"
    private fun keyMoves(level: Int) = "level_${level}_moves"

    companion object {
        private const val PREFS_NAME = "arrow_escape_progress_v1"
        private const val KEY_SFX_ENABLED = "settings_sfx_enabled"
        private const val KEY_MUSIC_ENABLED = "settings_music_enabled"
        private const val KEY_SFX_VOLUME = "settings_sfx_volume"
        private const val KEY_MUSIC_VOLUME = "settings_music_volume"
        private const val KEY_VIBRATION_ENABLED = "settings_vibration_enabled"
    }
}
