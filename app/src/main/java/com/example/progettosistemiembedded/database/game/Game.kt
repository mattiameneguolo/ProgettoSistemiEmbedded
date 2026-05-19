package com.example.progettosistemiembedded.database.game

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "games")
class Game (
    @PrimaryKey(autoGenerate = true)
    val id: Int = 1,
    val sequence: String,
    val errorIndex: Int,
)