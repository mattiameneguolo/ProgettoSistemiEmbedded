package com.example.progettosistemiembedded.database.game

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entità che rappresenta la struttura della tabella "games" nel database.
 *
 * Ogni istanza di questa classe rappresenta un'entità nel database,
 *
 * @property id = identificativo univoco nella partita, generato automaticamente
 * @property sequence = sequenza di caratteri che rappresenta la partita
 * @property errorIndex = indice del carattere di errore nella sequenza (-1 se non ci sono errori)
 */
@Entity(tableName = "games")
data class Game (
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val sequence: String,
    val errorIndex: Int,
)