package com.faqih.videogame

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import com.faqih.videogame.ui.screen.GameDetailScreen
import com.faqih.videogame.ui.theme.VideoGameTheme
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.faqih.videogame.ui.screen.HomeScreen
import com.faqih.videogame.viewmodel.GameViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            VideoGameTheme {
                GameExplorerApp()
            }
        }
    }
}

@Composable
fun GameExplorerApp() {

    val navController = rememberNavController()
    val gameViewModel: GameViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = "home"
    ) {

        composable("home") {

            HomeScreen(
                viewModel = gameViewModel,
                onGameClick = { gameId ->
                    navController.navigate("detail/$gameId")
                }
            )
        }

        composable(
            route = "detail/{gameId}",
            arguments = listOf(
                navArgument("gameId") {
                    type = NavType.IntType
                }
            )
        ) { backStackEntry ->

            val gameId = backStackEntry.arguments
                ?.getInt("gameId")
                ?: 0

            GameDetailScreen(
                viewModel = gameViewModel,
                gameId = gameId,
                onBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}