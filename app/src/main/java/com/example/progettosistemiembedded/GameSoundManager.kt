package com.example.progettosistemiembedded.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import com.example.progettosistemiembedded.R

/**
 * Classe manager degli effetti sonori dell'applicazione.
 *
 * Utilizza la libreria SoundPool per riprodurre file audio in formato .wav,
 * associando ogni suono al rispettivo carattere o all'errore.
 *
 * @param context contesto dell'applicazione per l'inizializzazione del SoundPool
 */
class GameSoundManager(context: Context) {

    private val soundPool: SoundPool

    /**
     * Mappa che associa un identificativo String al rispettivo suono.
     *
     * La chiave può essere uno dei 6 caratteri della matrice o la stringa "error"
     */
    private val soundMap = mutableMapOf<String, Int>()

    /**
     * Inizializza il SoundPool e carica tutti i suoni utilizzati nell'app.
     */
    init {
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(6)
            .setAudioAttributes(audioAttributes)
            .build()

        // Associazione suono <-> colore/errore
        soundMap["R"] = soundPool.load(context, R.raw.tone_r, 1)
        soundMap["G"] = soundPool.load(context, R.raw.tone_g, 1)
        soundMap["B"] = soundPool.load(context, R.raw.tone_b, 1)
        soundMap["M"] = soundPool.load(context, R.raw.tone_m, 1)
        soundMap["Y"] = soundPool.load(context, R.raw.tone_y, 1)
        soundMap["C"] = soundPool.load(context, R.raw.tone_c, 1)
        soundMap["error"] = soundPool.load(context, R.raw.error, 1)
    }

    /**
     * Member function che permette la riproduzione del suono associato all'ID
     * passato come parametro.
     *
     * @param soundId identificativo del suono da riprodurre
     */
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

    /**
     * Member function che rilascia i risorse utilizzate dal SoundPool.
     *
     * Metodo chiamato quando i suoni non sono più necessari, così da evitare sprechi di memoria
     */
    fun release() {
        soundPool.release()
    }
}