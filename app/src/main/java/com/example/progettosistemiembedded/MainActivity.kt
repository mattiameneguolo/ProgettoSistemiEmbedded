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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import android.util.Log
import androidx.navigation.NavBackStackEntry
import com.example.progettosistemiembedded.routes.game_details.GameDetailsScreen
import com.example.progettosistemiembedded.routes.game.GameScreen
import com.example.progettosistemiembedded.routes.results.ResultsScreen

class GameHistory: ViewModel () {
    var games by mutableStateOf<List<Game>>(emptyList())
        private set

    fun addGame(sequence: List<String>, errorIndex: Int) {
        games = games + listOf(Game(sequence, errorIndex, games.size + 1))
    }
}

class MainActivity : ComponentActivity() {

    val mTAG = this::class.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        Log.d(mTAG, "Creating MainActivity view")

        setContent {
            SimonGameTheme {

                val navController = rememberNavController()
                val gameHistory: GameHistory = viewModel()

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavHost(
                        navController = navController, startDestination = "results",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable("results") {
                            Log.d(mTAG, "Navigating to results screen with games: ${gameHistory.games}")
                            ResultsScreen(
                                modifier = Modifier,
                                gameHistory.games,
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
                                    gameHistory.addGame(sequence, errorIndex)
                                    navController.navigate("results") {
                                        popUpTo("results") {
                                            inclusive = true
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

                            Log.d(mTAG, "Navigating to game details screen for game: $gameID")
                            GameDetailsScreen(
                                modifier = Modifier,
                                gameID,
                                gameHistory.games[gameID - 1]
                            )
                        }
                    }
                }
            }
        }
    }
}
