package com.example.progettosistemiembedded

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.progettosistemiembedded.database.game.Game
import com.example.progettosistemiembedded.database.game.GameDao

@Database(entities = [Game::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun gameDao(): GameDao
}