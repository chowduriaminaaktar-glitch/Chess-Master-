package com.example.chess.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.os.Build
import android.os.CombinedVibration
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.exp
import kotlin.math.sin

class SoundHapticHelper(private val context: Context) {

    private val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val manager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
        manager?.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }

    private val scope = CoroutineScope(Dispatchers.Default)

    var soundEnabled: Boolean = true
    var hapticsEnabled: Boolean = true

    fun playMoveFeedback() {
        if (hapticsEnabled) {
            triggerVibration(30, 80)
        }
        if (soundEnabled) {
            scope.launch {
                playSynthTone(frequency = 240.0, durationMs = 50, decay = 0.08)
            }
        }
    }

    fun playCaptureFeedback() {
        if (hapticsEnabled) {
            triggerVibration(45, 180)
        }
        if (soundEnabled) {
            scope.launch {
                playSynthTone(frequency = 360.0, durationMs = 70, decay = 0.05, harmonicFactor = 0.4)
            }
        }
    }

    fun playCheckFeedback() {
        if (hapticsEnabled) {
            triggerVibration(80, 255)
        }
        if (soundEnabled) {
            scope.launch {
                playDualTone(f1 = 440.0, f2 = 587.33, durationMs = 120)
            }
        }
    }

    fun playGameOverFeedback() {
        if (hapticsEnabled) {
            triggerVibrationPattern(longArrayOf(0, 100, 80, 150))
        }
        if (soundEnabled) {
            scope.launch {
                playFanfare()
            }
        }
    }

    private fun triggerVibration(durationMs: Long, amplitude: Int) {
        try {
            if (vibrator?.hasVibrator() == true) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val effect = VibrationEffect.createOneShot(durationMs, amplitude.coerceIn(1, 255))
                    vibrator.vibrate(effect)
                } else {
                    @Suppress("DEPRECATION")
                    vibrator.vibrate(durationMs)
                }
            }
        } catch (_: Exception) { }
    }

    private fun triggerVibrationPattern(timings: LongArray) {
        try {
            if (vibrator?.hasVibrator() == true) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val effect = VibrationEffect.createWaveform(timings, -1)
                    vibrator.vibrate(effect)
                } else {
                    @Suppress("DEPRECATION")
                    vibrator.vibrate(timings, -1)
                }
            }
        } catch (_: Exception) { }
    }

    private fun playSynthTone(frequency: Double, durationMs: Int, decay: Double, harmonicFactor: Double = 0.0) {
        try {
            val sampleRate = 22050
            val numSamples = (sampleRate * (durationMs / 1000.0)).toInt().coerceAtLeast(100)
            val buffer = ShortArray(numSamples)

            for (i in 0 until numSamples) {
                val time = i.toDouble() / sampleRate
                val env = exp(-decay * i.toDouble())
                val primary = sin(2.0 * Math.PI * frequency * time)
                val harmonic = if (harmonicFactor > 0.0) sin(4.0 * Math.PI * frequency * time) * harmonicFactor else 0.0
                val sample = ((primary + harmonic) * env * 24000.0).toInt().coerceIn(-32767, 32767)
                buffer[i] = sample.toShort()
            }

            val track = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_GAME)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(buffer.size * 2)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            track.write(buffer, 0, buffer.size)
            track.play()
            Thread.sleep(durationMs.toLong() + 20)
            track.stop()
            track.release()
        } catch (_: Exception) { }
    }

    private fun playDualTone(f1: Double, f2: Double, durationMs: Int) {
        try {
            val sampleRate = 22050
            val halfSamples = (sampleRate * ((durationMs / 2) / 1000.0)).toInt().coerceAtLeast(100)
            val numSamples = halfSamples * 2
            val buffer = ShortArray(numSamples)

            for (i in 0 until halfSamples) {
                val time = i.toDouble() / sampleRate
                val env = exp(-0.04 * i.toDouble())
                val sample = (sin(2.0 * Math.PI * f1 * time) * env * 22000.0).toInt().coerceIn(-32767, 32767)
                buffer[i] = sample.toShort()
            }
            for (i in 0 until halfSamples) {
                val time = i.toDouble() / sampleRate
                val env = exp(-0.03 * i.toDouble())
                val sample = (sin(2.0 * Math.PI * f2 * time) * env * 22000.0).toInt().coerceIn(-32767, 32767)
                buffer[halfSamples + i] = sample.toShort()
            }

            val track = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_GAME)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(buffer.size * 2)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            track.write(buffer, 0, buffer.size)
            track.play()
            Thread.sleep(durationMs.toLong() + 20)
            track.stop()
            track.release()
        } catch (_: Exception) { }
    }

    private fun playFanfare() {
        val notes = listOf(523.25, 659.25, 783.99)
        for (freq in notes) {
            playSynthTone(frequency = freq, durationMs = 90, decay = 0.02)
            try { Thread.sleep(60) } catch (_: Exception) {}
        }
    }
}
