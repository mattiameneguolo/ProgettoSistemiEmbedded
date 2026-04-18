package com.example.progettosistemiembedded

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

class GameHistory: ViewModel () {
    var games by mutableStateOf<List<List<String>>>(emptyList())
        private set

    fun addGame(game: List<String>) {
        games = games + listOf(game)
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
                        navController = navController, startDestination = "main",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable("main") {
                            Log.d(mTAG, "Navigating to main screen")
                            MainScreen(modifier = Modifier, onGameEnd = { sequence ->
                                Log.d(mTAG, "Game ended with sequence $sequence")
                                gameHistory.addGame(sequence)
                                navController.navigate("results")
                            })
                        }
                        composable("results") {
                            Log.d(mTAG, "Navigating to results screen with games: ${gameHistory.games}")
                            ResultsScreen(modifier = Modifier, gameHistory.games)
                        }
                    }
                }
            }
        }
    }
}
