package com.kinetix.app.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.kinetix.app.data.AuthSession

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController
) {
    var selectedItem by remember {
        mutableIntStateOf(0)
    }

    val items = listOf(
        "Dashboard",
        "Workout",
        "Profile",
        "Settings"
    )

    val icons = listOf(
        Icons.Filled.Home,
        Icons.Filled.FitnessCenter,
        Icons.Filled.Person,
        Icons.Filled.Settings
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Kinetix")
                },
                colors = TopAppBarDefaults.topAppBarColors()
            )
        },
        bottomBar = {
            NavigationBar {
                items.forEachIndexed { index, label ->
                    NavigationBarItem(
                        selected = selectedItem == index,
                        onClick = {
                            selectedItem = index
                        },
                        icon = {
                            Icon(
                                imageVector = icons[index],
                                contentDescription = label
                            )
                        },
                        label = {
                            Text(label)
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        when (selectedItem) {
            0 -> DashboardScreen(
                firstName = AuthSession.current
                    ?.firstName
                    .orEmpty(),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp)
            )

            1 -> ExerciseSelectionScreen(
                navController = navController
            )

            2 -> ProfileScreen(
                navController = navController
            )

            3 -> SettingsScreen()
        }
    }
}