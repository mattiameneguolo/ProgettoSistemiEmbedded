package com.example.progettosistemiembedded.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import com.example.progettosistemiembedded.R

class GameSoundManager(context: Context) {

    private val soundPool: SoundPool

    private val soundMap = mutableMapOf<String, Int>()

    init {
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(6)
            .setAudioAttributes(audioAttributes)
            .build()

        soundMap["R"] = soundPool.load(context, R.raw.tone_r, 1)
        soundMap["G"] = soundPool.load(context, R.raw.tone_g, 1)
        soundMap["B"] = soundPool.load(context, R.raw.tone_b, 1)
        soundMap["M"] = soundPool.load(context, R.raw.tone_m, 1)
        soundMap["Y"] = soundPool.load(context, R.raw.tone_y, 1)
        soundMap["C"] = soundPool.load(context, R.raw.tone_c, 1)
        soundMap["error"] = soundPool.load(context, R.raw.error, 1)
    }

    fun play(soundId: String) {
        val resId = soundMap[soundId] ?: return

        soundPool.play(
            resId,
            1f,     // volume sinistro
            1f,     // volume destro
            1,      // priority
            0,      // loop: 0 = una volta sola
            1f      // rate: 1f = velocità normale
        )
    }

    fun release() {
        soundPool.release()
    }
}