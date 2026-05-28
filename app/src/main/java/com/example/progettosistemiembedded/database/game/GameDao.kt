package com.example.progettosistemiembedded.database.game

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

/**
 * Dao (Data Access Object) per l'interazione con la tabella "games" nel database.
 */
@Dao
interface GameDao {

    /**
     * Recupera tutte le partite salvate nel database.
     *
     * @return lista di oggetti Game salvati nella tabella "games"
     */
    @Query("SELECT * FROM games")
    suspend fun getAllGames(): List<Game>

    /**
     * Recupera una singola partita dal database in base all'ID.
     *
     * @param gameId = identificativo della partita da cercare
     * @return oggetto Game corrispondente all'ID fornito
     */
    @Query("SELECT * FROM games WHERE id = :gameId")
    suspend fun getGameById(gameId: Int): Game

    /**
     * Inserisce una nuova partita nel database.
     *
     * @param game = oggetto Game da inserire nel database
     */
    @Insert
    suspend fun insertGame(game: Game)
}