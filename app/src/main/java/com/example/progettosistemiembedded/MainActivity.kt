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

class MainActivity : ComponentActivity() {

    val mTAG = this::class.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "simon-2k26-db"
        ).build()

        Log.d(mTAG, "Creating MainActivity view")

        setContent {
            SimonGameTheme {

                val navController = rememberNavController()
                val scope = rememberCoroutineScope()

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavHost(
                        navController = navController, startDestination = "results",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable("results") {
                            var games by remember {
                                mutableStateOf<List<Game>>(emptyList())
                            }

                            LaunchedEffect(Unit) {
                                games = db.gameDao().getAllGames()
                            }

                            Log.d(mTAG, "Navigating to results screen with games: $games")

                            ResultsScreen(
                                modifier = Modifier,
                                games = games,
                                onGameClick = { gameId ->
                                    navController.navigate("game_details/${Uri.encode(gameId.toString())}")
                                },
                                onNewGameClick = {
                                    navController.navigate("game")
                                }
                            )
                        }
                        composable("game") {
                            Log.d(mTAG, "Navigating to game screen")
                            GameScreen(
                                modifier = Modifier,
                                onGameEnd = { sequence, errorIndex ->
                                    Log.d(mTAG, "Game ended with sequence $sequence, errorIndex: $errorIndex")

                                    val game = Game(
                                        sequence = sequence.joinToString(","),
                                        errorIndex = errorIndex
                                    )

                                    scope.launch {
                                        db.gameDao().insertGame(game)

                                        navController.navigate("results") {
                                            popUpTo("results") {
                                                inclusive = true
                                            }
                                        }
                                    }
                                },
                                onGameCanceled = {
                                    navController.navigate("results") {
                                        popUpTo("results") {
                                            inclusive = true
                                        }
                                    }
                                }
                            )
                        }
                        composable("game_details/{gameId}") { backStackEntry: NavBackStackEntry ->
                            val gameID = backStackEntry.arguments?.getString("gameId").orEmpty().toInt()

                            var game by remember {
                                mutableStateOf<Game?>(null)
                            }

                            LaunchedEffect(gameID) {
                                game = db.gameDao().getGameById(gameID)
                            }

                            Log.d(mTAG, "Navigating to game details screen for game: $gameID")

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
