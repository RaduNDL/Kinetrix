package com.kinetix.app.ui.navigation

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.kinetix.app.ui.screens.ExerciseSelectionScreen
import com.kinetix.app.ui.screens.HomeScreen
import com.kinetix.app.ui.screens.LoginScreen
import com.kinetix.app.ui.screens.RegisterScreen
import com.kinetix.app.ui.screens.VerifyEmailScreen
import com.kinetix.app.ui.screens.WorkoutCameraScreen
import com.kinetix.app.ui.screens.WorkoutSummaryScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        composable("login") {
            LoginScreen(navController)
        }

        composable("register") {
            RegisterScreen(navController)
        }

        composable(
            route = "verify/{email}",
            arguments = listOf(
                navArgument("email") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val encodedEmail =
                backStackEntry.arguments
                    ?.getString("email")
                    .orEmpty()

            VerifyEmailScreen(
                navController = navController,
                email = Uri.decode(encodedEmail)
            )
        }

        composable("home") {
            HomeScreen(navController)
        }

        composable("exercise-selection") {
            ExerciseSelectionScreen(navController)
        }

        composable(
            route = "workout-camera/{exerciseName}",
            arguments = listOf(
                navArgument("exerciseName") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val exerciseName =
                backStackEntry.arguments
                    ?.getString("exerciseName")
                    .orEmpty()

            WorkoutCameraScreen(
                navController = navController,
                exerciseName = exerciseName
            )
        }

        composable("workout-summary") {
            WorkoutSummaryScreen(navController)
        }
    }
}