package com.djembe.android.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import com.djembe.android.R

/**
 * Handles loading and low-latency playback of the three core djembe tones:
 * BASS (deep, center of drum), TONE (mid, edge with open hand), SLAP (sharp, fingers).
 *
 * SoundPool is used instead of MediaPlayer because it's designed for short,
 * latency-sensitive sample playback (game/instrument-style triggering) rather
 * than streaming - MediaPlayer has too much trigger latency for a playable instrument.
 */
class DjembeSoundEngine(context: Context) {

    enum class Tone { BASS, TONE, SLAP }

    private val appContext = context.applicationContext

    private val soundPool: SoundPool = SoundPool.Builder()
        .setMaxStreams(6) // allow several rapid hits to overlap naturally
        .setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME) // lowest-latency audio attribute usage
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
        )
        .build()

    private val soundIds = mutableMapOf<Tone, Int>()
    private var loaded = false

    init {
        soundPool.setOnLoadCompleteListener { _, _, status ->
            if (status == 0) loaded = true
        }
        // Sample files go in res/raw/ as djembe_bass.wav, djembe_tone.wav, djembe_slap.wav
        // Short, dry-mixed WAV samples give the lowest trigger latency (avoid mp3 here).
        soundIds[Tone.BASS] = soundPool.load(appContext, R.raw.djembe_bass, 1)
        soundIds[Tone.TONE] = soundPool.load(appContext, R.raw.djembe_tone, 1)
        soundIds[Tone.SLAP] = soundPool.load(appContext, R.raw.djembe_slap, 1)
    }

    /**
     * Trigger a hit. [velocity] is 0f-1f (e.g. derived from touch pressure or
     * position on the drum head) and maps to playback volume for basic dynamics.
     */
    fun play(tone: Tone, velocity: Float = 1f) {
        val id = soundIds[tone] ?: return
        val vol = velocity.coerceIn(0.2f, 1f) // floor so hits are never silent
        soundPool.play(id, vol, vol, /* priority = */ 1, /* loop = */ 0, /* rate = */ 1f)
    }

    fun release() {
        soundPool.release()
    }
}
