package com.example.data

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import com.example.model.AudioTrack as AudioTrackModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.sin

object AudioCatalog {
    val availableTracks = listOf(
        AudioTrackModel(
            id = "synth_sunset",
            title = "Synthwave Sunset",
            artist = "RetroWave Lab",
            genre = "Electronic / 80s",
            durationSec = 30f,
            bpm = 120,
            noteFrequencies = listOf(261, 329, 392, 523, 440, 349) // C4, E4, G4, C5, A4, F4
        ),
        AudioTrackModel(
            id = "lofi_chill",
            title = "Lo-Fi Midnight Rain",
            artist = "Coffee Shop Beats",
            genre = "Lo-Fi Chill",
            durationSec = 30f,
            bpm = 85,
            noteFrequencies = listOf(220, 261, 329, 392) // A3, C4, E4, G4 (Am7)
        ),
        AudioTrackModel(
            id = "vlog_upbeat",
            title = "Sunlight Energy",
            artist = "Creator Moods",
            genre = "Upbeat Pop",
            durationSec = 30f,
            bpm = 128,
            noteFrequencies = listOf(293, 369, 440, 587) // D4, F#4, A4, D5
        ),
        AudioTrackModel(
            id = "cinematic_hero",
            title = "Epic Odyssey",
            artist = "Orchestral Forge",
            genre = "Cinematic Trailer",
            durationSec = 30f,
            bpm = 100,
            noteFrequencies = listOf(196, 246, 293, 392) // G3, B3, D4, G4
        ),
        AudioTrackModel(
            id = "acoustic_journey",
            title = "Warm Campfire",
            artist = "Acoustic Tales",
            genre = "Acoustic Folk",
            durationSec = 30f,
            bpm = 95,
            noteFrequencies = listOf(329, 392, 493, 659) // E4, G4, B4, E5
        )
    )

    val soundEffects = listOf(
        "Whoosh" to 800,
        "Clapper" to 1200,
        "Pop" to 1500,
        "Ding" to 2200,
        "Camera Click" to 3000
    )
}

/**
 * Real-time software audio synthesizer for previewing audio rhythmically
 */
class StudioAudioSynthesizer {
    private var nativeAudioTrack: AudioTrack? = null
    private var isPlaying = false

    suspend fun playTrackLoop(track: AudioTrackModel, durationSec: Float) = withContext(Dispatchers.Default) {
        stop()
        isPlaying = true

        val sampleRate = 22050
        val bufferSize = AudioTrack.getMinBufferSize(
            sampleRate,
            AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        ).coerceAtLeast(4096)

        try {
            nativeAudioTrack = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(bufferSize)
                .setTransferMode(AudioTrack.MODE_STREAM)
                .build()

            nativeAudioTrack?.play()

            val notes = track.noteFrequencies.ifEmpty { listOf(440) }
            val beatDurationMs = (60_000 / track.bpm).coerceIn(200, 800)
            val samplesPerBeat = (sampleRate * (beatDurationMs / 1000f)).toInt()

            var noteIdx = 0
            val totalBeats = ((durationSec * 1000) / beatDurationMs).toInt().coerceAtLeast(1)

            for (beat in 0 until totalBeats) {
                if (!isPlaying) break

                val freq = notes[noteIdx % notes.size]
                noteIdx++

                val pcmBuffer = ShortArray(samplesPerBeat)
                for (i in 0 until samplesPerBeat) {
                    val time = i.toDouble() / sampleRate
                    // Apply attack and decay envelope
                    val envelope = (1.0 - (i.toDouble() / samplesPerBeat)).coerceIn(0.0, 1.0)
                    // Fundamental + subtle harmonic
                    val wave = (sin(2.0 * Math.PI * freq * time) * 0.7 +
                            sin(2.0 * Math.PI * (freq * 2) * time) * 0.3) * envelope
                    pcmBuffer[i] = (wave * Short.MAX_VALUE * 0.35f * track.volume).toInt().toShort()
                }

                nativeAudioTrack?.write(pcmBuffer, 0, pcmBuffer.size)
            }
        } catch (_: Exception) {
            // AudioTrack failure handled safely
        }
    }

    suspend fun playSfx(freqHz: Int) = withContext(Dispatchers.Default) {
        val sampleRate = 22050
        val durationMs = 120
        val numSamples = (sampleRate * (durationMs / 1000f)).toInt()
        val pcm = ShortArray(numSamples)
        for (i in 0 until numSamples) {
            val t = i.toDouble() / sampleRate
            val envelope = (1.0 - (i.toDouble() / numSamples))
            val wave = sin(2.0 * Math.PI * freqHz * t) * envelope
            pcm[i] = (wave * Short.MAX_VALUE * 0.4f).toInt().toShort()
        }
        try {
            val track = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
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
                .setBufferSizeInBytes(pcm.size * 2)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()
            track.write(pcm, 0, pcm.size)
            track.play()
        } catch (_: Exception) {}
    }

    fun stop() {
        isPlaying = false
        try {
            nativeAudioTrack?.stop()
            nativeAudioTrack?.release()
        } catch (_: Exception) {}
        nativeAudioTrack = null
    }
}
