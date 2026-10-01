package com.kinetix.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.kinetix.app.data.pose.ExerciseType

@Composable
fun WorkoutCameraScreen(
    navController: NavController,
    exerciseName: String
) {
    val exercise = runCatching {
        ExerciseType.valueOf(exerciseName)
    }.getOrDefault(ExerciseType.SQUAT)

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = exercise.displayName,
                style = MaterialTheme.typography.headlineMedium
            )

            Text(
                text = "Camera and MediaPipe analysis will be connected here."
            )

            Button(
                onClick = {
                    navController.navigate("workout-summary")
                }
            ) {
                Text("Finish workout")
            }
        }
    }
}