package com.example.progettosistemiembedded

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.progettosistemiembedded.database.game.Game
import com.example.progettosistemiembedded.database.game.GameDao

/**
 * Classe che rappresenta il database dell'applicazione.
 * La classe utilizza Room per gestire la persistenza dei dati.
 *
 * Il database contiene solamente un'entità: Game
 */
@Database(entities = [Game::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    /**
     * Dao utilizzato per accedere ai dati dell'entità Game.
     *
     * @return Istanza di GameDao
     */
    abstract fun gameDao(): GameDao
}