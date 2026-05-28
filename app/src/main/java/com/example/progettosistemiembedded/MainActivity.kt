package com.example.progettosistemiembedded

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.progettosistemiembedded.ui.theme.SimonGameTheme
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import android.util.Log
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavBackStackEntry
import com.example.progettosistemiembedded.database.game.Game
import com.example.progettosistemiembedded.routes.game_details.GameDetailsScreen
import com.example.progettosistemiembedded.routes.game.GameScreen
import com.example.progettosistemiembedded.routes.results.ResultsScreen
import androidx.room.Room
import kotlinx.coroutines.launch

/**
 * Activity principale dell'applicazione.
 *
 * Si occupa di:
 * - inizializzare il database Room;
 * - configurare il tema grafico dell'app;
 * - gestire la navigazione tra diverse schermate;
 * - salvare le partite concluse nel database locale.
 */
class MainActivity : ComponentActivity() {

    private val mTAG = this::class.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Creazione del database Room dell'applicazione
        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "simon-2k26-db"
        ).build()

        Log.d(mTAG, "Creating MainActivity view")

        setContent {
            SimonGameTheme {

                // Controller usato per gestire la navigazione tra le schermate.
                val navController = rememberNavController()

                /**
                 * CoroutineScope usata per eseguire operazioni asincrone,
                 * come l'inserimento dei dati nel database
                 */
                val scope = rememberCoroutineScope()

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

                    /**
                     * NavHost per gestire la navigazione tra le diverse schermate.
                     *
                     * La schermata principale è "results", cioè la lista delle partite
                     * salvate.
                     */
                    NavHost(
                        navController = navController, startDestination = "results",
                        modifier = Modifier.padding(innerPadding)
                    ) {

                        /**
                         * Route "/results" rappresenta la schermata dei risultati
                         * salvati nel database.
                         */
                        composable("results") {
                            var games by remember {
                                mutableStateOf<List<Game>>(emptyList())
                            }

                            /**
                             * Quando la schermata viene caricata, recupera tutte
                             * le partite salvate nel database tramite la gameDao.
                             */
                            LaunchedEffect(Unit) {
                                games = db.gameDao().getAllGames()
                            }

                            Log.d(mTAG, "Navigating to results screen with games: $games")

                            ResultsScreen(
                                modifier = Modifier,
                                games = games,
                                onGameClick = { gameId ->
                                    // Navigazione verso la schermata dei dettagli della partita:
                                    // /game_details/{gameId}
                                    navController.navigate("game_details/${Uri.encode(gameId.toString())}")
                                },
                                onNewGameClick = {
                                    // Navigazione verso la schermata di gioco: /game
                                    navController.navigate("game")
                                }
                            )
                        }

                        /**
                         * Route "/game" che rappresenta la schermata di gioco
                         */
                        composable("game") {
                            Log.d(mTAG, "Navigating to game screen")
                            GameScreen(
                                modifier = Modifier,
                                onGameEnd = { sequence, errorIndex ->
                                    // Callback eseguita al termine (salvataggio) di una partita

                                    Log.d(mTAG, "Game ended with sequence $sequence, errorIndex: $errorIndex")

                                    // Creazione oggetto Game da salvare nel db
                                    val game = Game(
                                        sequence = sequence.joinToString(","),
                                        errorIndex = errorIndex
                                    )

                                    // Inserimento (asincrono del Game nel database)
                                    scope.launch {
                                        db.gameDao().insertGame(game)

                                        /**
                                         * Dopo il salvataggio reindirizza alla pagina dei risultati.
                                         * Ripulisce il BackStack
                                         */
                                        navController.navigate("results") {
                                            popUpTo("results") {
                                                inclusive = true
                                            }
                                        }
                                    }
                                },
                                onGameCanceled = {
                                    // Calback eseguita al termine (annullamento) di una partita

                                    // Naviga alla pagina dei risultati senza salvare nulla nel db
                                    navController.navigate("results") {
                                        popUpTo("results") {
                                            inclusive = true
                                        }
                                    }
                                }
                            )
                        }

                        /**
                         * Route "/game_details/{gameId}" che rappresenta la schermata dei dettagli
                         * di una singola partita.
                         *
                         * Riceve l'id della partita tramite il percorso di navigazione
                         * e recupera dal database la partita corrispondente.
                         */
                        composable("game_details/{gameId}") { backStackEntry: NavBackStackEntry ->
                            val gameID = backStackEntry.arguments?.getString("gameId").orEmpty().toInt()

                            var game by remember {
                                mutableStateOf<Game?>(null)
                            }

                            // Carica dal database la partita corrispondente all'ID
                            LaunchedEffect(gameID) {
                                game = db.gameDao().getGameById(gameID)
                            }

                            Log.d(mTAG, "Navigating to game details screen for game: $gameID")

                            /**
                             * Mostra la schermata di dettaglio solo quando e
                             * se la partita cercata esiste.
                             */
                            game?.let {
                                GameDetailsScreen(
                                    modifier = Modifier,
                                    game = it
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
