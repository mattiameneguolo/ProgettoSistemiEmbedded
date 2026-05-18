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

class GameHistory: ViewModel () {
    var games by mutableStateOf<List<Game>>(emptyList())
        private set

    fun addGame(sequence: List<String>) {
        games = games + listOf(Game(sequence, games.size + 1))
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
                            ResultsScreen(modifier = Modifier, gameHistory.games, onGameClick = { gameId ->
                                navController.navigate("game_details/${Uri.encode(gameId.toString())}")
                            })
                        }
                        composable("game") {
                            Log.d(mTAG, "Navigating to game screen")
                            GameScreen(modifier = Modifier, onGameEnd = { sequence ->
                                Log.d(mTAG, "Game ended with sequence $sequence")
                                gameHistory.addGame(sequence)
                                navController.navigate("results")
                            })
                        }
                        composable("game_details/{gameId}") { backStackEntry: NavBackStackEntry ->
                            val gameID = backStackEntry.arguments?.getString("gameId").orEmpty().toInt()

                            Log.d(mTAG, "Navigating to game details screen for game: $gameID")
                            GameDetailsScreen(modifier = Modifier, gameID, gameHistory.games[gameID-1])
                        }
                    }
                }
            }
        }
    }
}
