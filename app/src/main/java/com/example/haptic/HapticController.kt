package com.example.haptic

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

class HapticController(context: Context) {

    @Suppress("DEPRECATION")
    private val vibrator: Vibrator? = runCatching {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val manager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            manager?.defaultVibrator
        } else {
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
    }.getOrNull()

    fun lightTap(enabled: Boolean) {
        if (!enabled) return
        vibrateOneShot(durationMs = 14L, amplitude = 70)
    }

    fun escapePulse(enabled: Boolean) {
        if (!enabled) return
        vibrateOneShot(durationMs = 22L, amplitude = 110)
    }

    fun blockedBuzz(enabled: Boolean) {
        if (!enabled) return
        runCatching {
            val v = vibrator ?: return
            if (!v.hasVibrator()) return
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val timings = longArrayOf(0, 28, 35, 36)
                val amplitudes = intArrayOf(0, 140, 0, 110)
                v.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))
            } else {
                @Suppress("DEPRECATION")
                v.vibrate(longArrayOf(0, 28, 35, 36), -1)
            }
        }
    }

    fun victoryPulse(enabled: Boolean) {
        if (!enabled) return
        runCatching {
            val v = vibrator ?: return
            if (!v.hasVibrator()) return
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val timings = longArrayOf(0, 30, 55, 45, 55, 70)
                val amplitudes = intArrayOf(0, 110, 0, 160, 0, 220)
                v.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))
            } else {
                @Suppress("DEPRECATION")
                v.vibrate(longArrayOf(0, 30, 55, 45, 55, 70), -1)
            }
        }
    }

    private fun vibrateOneShot(durationMs: Long, amplitude: Int) {
        runCatching {
            val v = vibrator ?: return
            if (!v.hasVibrator()) return
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                v.vibrate(VibrationEffect.createOneShot(durationMs, amplitude))
            } else {
                @Suppress("DEPRECATION")
                v.vibrate(durationMs)
            }
        }
    }
}
