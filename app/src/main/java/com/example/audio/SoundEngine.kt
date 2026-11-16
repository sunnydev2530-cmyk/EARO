package com.example.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import java.util.concurrent.Executors
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin

enum class SoundEffect {
    BUTTON_CLICK,
    ARROW_SELECT,
    ARROW_ESCAPE,
    BLOCKED_ARROW,
    LEVEL_COMPLETE,
    LEVEL_UNLOCKED,
    HINT,
    UNDO,
    VICTORY
}

class SoundEngine {

    private val executor = Executors.newSingleThreadExecutor()
    private val sfxTracks = HashMap<SoundEffect, AudioTrack>()
    private var musicTrack: AudioTrack? = null

    @Volatile
    private var initialized = false

    @Volatile
    private var sfxEnabled = true

    @Volatile
    private var musicEnabled = true

    @Volatile
    private var sfxVolume = 0.85f

    @Volatile
    private var musicVolume = 0.35f

    @Volatile
    private var appForeground = true

    fun initialize() {
        if (initialized) return
        executor.execute {
            if (initialized) return@execute
            runCatching {
                for (effect in SoundEffect.entries) {
                    val samples = synthesizeEffect(effect)
                    createStaticTrack(samples)?.let { track ->
                        sfxTracks[effect] = track
                    }
                }
                val ambientLoop = synthesizeAmbientMusic()
                createStaticTrack(ambientLoop, loop = true)?.let { track ->
                    musicTrack = track
                    track.setVolume(musicVolume)
                    if (musicEnabled && appForeground) {
                        track.play()
                    }
                }
            }
            initialized = true
        }
    }

    fun updateSettings(
        sfxEnabled: Boolean,
        musicEnabled: Boolean,
        sfxVolume: Float,
        musicVolume: Float
    ) {
        this.sfxEnabled = sfxEnabled
        this.musicEnabled = musicEnabled
        this.sfxVolume = sfxVolume.coerceIn(0f, 1f)
        this.musicVolume = musicVolume.coerceIn(0f, 1f)

        executor.execute {
            runCatching {
                musicTrack?.let { track ->
                    track.setVolume(this.musicVolume)
                    if (this.musicEnabled && appForeground) {
                        if (track.playState != AudioTrack.PLAYSTATE_PLAYING) {
                            track.play()
                        }
                    } else {
                        if (track.playState == AudioTrack.PLAYSTATE_PLAYING) {
                            track.pause()
                        }
                    }
                }
            }
        }
    }

    fun onForegroundChanged(isForeground: Boolean) {
        appForeground = isForeground
        executor.execute {
            runCatching {
                musicTrack?.let { track ->
                    if (isForeground && musicEnabled) {
                        track.setVolume(musicVolume)
                        if (track.playState != AudioTrack.PLAYSTATE_PLAYING) {
                            track.play()
                        }
                    } else {
                        if (track.playState == AudioTrack.PLAYSTATE_PLAYING) {
                            track.pause()
                        }
                    }
                }
            }
        }
    }

    fun play(effect: SoundEffect) {
        if (!sfxEnabled || sfxVolume <= 0.01f) return
        executor.execute {
            runCatching {
                val track = sfxTracks[effect] ?: return@runCatching
                if (track.playState == AudioTrack.PLAYSTATE_PLAYING) {
                    track.stop()
                }
                track.reloadStaticData()
                track.setVolume(sfxVolume)
                track.play()
            }
        }
    }

    fun release() {
        executor.execute {
            runCatching {
                musicTrack?.stop()
                musicTrack?.release()
                musicTrack = null
                sfxTracks.values.forEach { track ->
                    track.stop()
                    track.release()
                }
                sfxTracks.clear()
            }
        }
    }

    private fun createStaticTrack(samples: ShortArray, loop: Boolean = false): AudioTrack? {
        return runCatching {
            val byteSize = samples.size * 2
            val attributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
            val format = AudioFormat.Builder()
                .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                .setSampleRate(SAMPLE_RATE)
                .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                .build()
            val track = AudioTrack.Builder()
                .setAudioAttributes(attributes)
                .setAudioFormat(format)
                .setBufferSizeInBytes(byteSize)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()
            track.write(samples, 0, samples.size)
            if (loop) {
                track.setLoopPoints(0, samples.size, -1)
            }
            track
        }.getOrNull()
    }

    private fun synthesizeEffect(effect: SoundEffect): ShortArray {
        return when (effect) {
            SoundEffect.BUTTON_CLICK -> renderTones(
                durationSec = 0.075,
                generator = { t ->
                    val env = exp(-t * 58.0) * smoothAttack(t, 0.004)
                    (sin(2.0 * PI * 760.0 * t) * 0.65 + sin(2.0 * PI * 1140.0 * t) * 0.35) * env * 0.55
                }
            )
            SoundEffect.ARROW_SELECT -> renderTones(
                durationSec = 0.09,
                generator = { t ->
                    val env = exp(-t * 44.0) * smoothAttack(t, 0.005)
                    sin(2.0 * PI * 920.0 * t) * env * 0.5
                }
            )
            SoundEffect.ARROW_ESCAPE -> renderTones(
                durationSec = 0.24,
                generator = { t ->
                    val env = exp(-t * 14.0) * smoothAttack(t, 0.008)
                    val freq = 480.0 + 620.0 * (t / 0.24)
                    val shimmer = sin(2.0 * PI * freq * t) * 0.7 + sin(2.0 * PI * (freq * 1.5) * t) * 0.3
                    shimmer * env * 0.65
                }
            )
            SoundEffect.BLOCKED_ARROW -> renderTones(
                durationSec = 0.18,
                generator = { t ->
                    val env = exp(-t * 22.0) * smoothAttack(t, 0.005)
                    val f1 = 195.0 - 45.0 * (t / 0.18)
                    val f2 = 262.0 - 60.0 * (t / 0.18)
                    (sin(2.0 * PI * f1 * t) * 0.65 + sin(2.0 * PI * f2 * t) * 0.35) * env * 0.65
                }
            )
            SoundEffect.UNDO -> renderTones(
                durationSec = 0.19,
                generator = { t ->
                    val env = exp(-t * 18.0) * smoothAttack(t, 0.006)
                    val freq = 720.0 - 280.0 * (t / 0.19)
                    (sin(2.0 * PI * freq * t) * 0.75 + sin(2.0 * PI * freq * 0.5 * t) * 0.25) * env * 0.55
                }
            )
            SoundEffect.HINT -> renderArpeggio(
                notesHz = doubleArrayOf(880.0, 1174.66, 1318.51),
                stepSec = 0.075,
                noteDurationSec = 0.24,
                gain = 0.55
            )
            SoundEffect.LEVEL_UNLOCKED -> renderArpeggio(
                notesHz = doubleArrayOf(659.25, 987.77),
                stepSec = 0.09,
                noteDurationSec = 0.28,
                gain = 0.55
            )
            SoundEffect.LEVEL_COMPLETE -> renderArpeggio(
                notesHz = doubleArrayOf(523.25, 659.25, 783.99, 1046.50),
                stepSec = 0.085,
                noteDurationSec = 0.34,
                gain = 0.65
            )
            SoundEffect.VICTORY -> renderArpeggio(
                notesHz = doubleArrayOf(523.25, 659.25, 783.99, 987.77, 1318.51),
                stepSec = 0.09,
                noteDurationSec = 0.45,
                gain = 0.68
            )
        }
    }

    private fun synthesizeAmbientMusic(): ShortArray {
        // 12-second seamless calm futuristic ambient puzzle loop (4 bars x 3.0s)
        val durationSec = 12.0
        val count = (SAMPLE_RATE * durationSec).toInt()
        val buffer = DoubleArray(count)

        // Warm atmospheric progression: Cmaj9 -> Am9 -> Fmaj9 -> G6
        val chords = arrayOf(
            doubleArrayOf(130.81, 196.00, 246.94, 293.66, 329.63),
            doubleArrayOf(110.00, 164.81, 246.94, 261.63, 329.63),
            doubleArrayOf(87.31, 174.61, 220.00, 261.63, 329.63),
            doubleArrayOf(98.00, 196.00, 246.94, 293.66, 329.63)
        )
        val barSec = 3.0

        for (i in 0 until count) {
            val t = i.toDouble() / SAMPLE_RATE
            val barIdx = ((t / barSec).toInt()).coerceIn(0, chords.lastIndex)
            val localT = t - barIdx * barSec
            val chord = chords[barIdx]

            // Smooth crossfade pad envelope per 3-second bar
            val padEnv = sin(PI * (localT / barSec)).coerceAtLeast(0.0)
            var padSample = 0.0
            for (n in chord.indices) {
                val freq = chord[n]
                val weight = if (n == 0) 0.32 else 0.16
                padSample += sin(2.0 * PI * freq * t) * weight
            }
            padSample *= (0.45 + 0.55 * padEnv)

            // Gentle crystalline pulse every 0.75s
            val beatPeriod = 0.75
            val beatIdx = (t / beatPeriod).toInt()
            val beatT = t - beatIdx * beatPeriod
            val bellFreq = chord[(beatIdx % (chord.size - 1)) + 1] * 2.0
            val bellEnv = exp(-beatT * 4.5) * smoothAttack(beatT, 0.015)
            val bellSample = sin(2.0 * PI * bellFreq * t) * bellEnv * 0.18

            // Global loop boundary micro-fade to guarantee zero-click looping
            val edgeFade = when {
                t < 0.06 -> t / 0.06
                durationSec - t < 0.06 -> (durationSec - t) / 0.06
                else -> 1.0
            }

            buffer[i] = (padSample * 0.48 + bellSample) * edgeFade
        }

        return toPcm16(buffer)
    }

    private fun renderTones(durationSec: Double, generator: (Double) -> Double): ShortArray {
        val count = (SAMPLE_RATE * durationSec).toInt().coerceAtLeast(1)
        val out = DoubleArray(count)
        for (i in 0 until count) {
            val t = i.toDouble() / SAMPLE_RATE
            out[i] = generator(t)
        }
        return toPcm16(out)
    }

    private fun renderArpeggio(
        notesHz: DoubleArray,
        stepSec: Double,
        noteDurationSec: Double,
        gain: Double
    ): ShortArray {
        val totalSec = (notesHz.size - 1) * stepSec + noteDurationSec
        val count = (SAMPLE_RATE * totalSec).toInt().coerceAtLeast(1)
        val out = DoubleArray(count)
        for (idx in notesHz.indices) {
            val startSample = (idx * stepSec * SAMPLE_RATE).toInt()
            val noteSamples = (noteDurationSec * SAMPLE_RATE).toInt()
            val freq = notesHz[idx]
            for (j in 0 until noteSamples) {
                val pos = startSample + j
                if (pos >= count) break
                val lt = j.toDouble() / SAMPLE_RATE
                val env = exp(-lt * 10.5) * smoothAttack(lt, 0.008)
                val tone = sin(2.0 * PI * freq * lt) * 0.75 + sin(2.0 * PI * freq * 2.0 * lt) * 0.25
                out[pos] += tone * env * (gain / 1.4)
            }
        }
        return toPcm16(out)
    }

    private fun smoothAttack(t: Double, attackSec: Double): Double =
        if (t < attackSec) (t / attackSec).coerceIn(0.0, 1.0) else 1.0

    private fun toPcm16(samples: DoubleArray): ShortArray {
        val pcm = ShortArray(samples.size)
        for (i in samples.indices) {
            val clamped = samples[i].coerceIn(-0.95, 0.95)
            pcm[i] = (clamped * Short.MAX_VALUE).toInt().toShort()
        }
        return pcm
    }

    companion object {
        private const val SAMPLE_RATE = 22050
    }
}
