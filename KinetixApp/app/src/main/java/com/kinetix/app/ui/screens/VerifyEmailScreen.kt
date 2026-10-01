package com.kinetix.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.TextButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.kinetix.app.ui.components.*
import com.kinetix.app.ui.viewmodels.AuthViewModel
import kotlinx.coroutines.delay

@Composable
fun VerifyEmailScreen(
    navController: NavController,
    email: String,
    initialNotice: String = "",
    authViewModel: AuthViewModel = viewModel()
) {
    var code by rememberSaveable(email) { mutableStateOf("") }
    var resendSeconds by rememberSaveable(email) { mutableIntStateOf(0) }

    LaunchedEffect(authViewModel.resendVersion) {
        if (authViewModel.resendVersion > 0) resendSeconds = 60
    }
    LaunchedEffect(resendSeconds) {
        if (resendSeconds > 0) {
            delay(1000)
            resendSeconds--
        }
    }
    LaunchedEffect(authViewModel.isSuccess) {
        if (authViewModel.isSuccess) {
            authViewModel.resetState()
            navController.navigate("home") {
                popUpTo("login") { inclusive = true }
                launchSingleTop = true
            }
        }
    }

    AuthBackground {
        Column(
            modifier = Modifier.fillMaxSize().imePadding()
                .verticalScroll(rememberScrollState()).padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(18.dp, Alignment.CenterVertically)
        ) {
            KinetixBrand()
            AuthTitle("Verify your email", "Enter the code for $email. If you haven't received it, check Spam or request a new code.")
            if (initialNotice.isNotBlank() && authViewModel.resendVersion == 0 && authViewModel.errorMessage == null) {
                AuthMessage(initialNotice, isError = true)
            }
            AuthField(
                value = code,
                onValueChange = {
                    code = it.filter { character -> character in '0'..'9' }.take(6)
                    authViewModel.clearMessages()
                },
                label = "Six-digit code",
                leadingIcon = Icons.Filled.Lock,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            authViewModel.errorMessage?.let { AuthMessage(it, isError = true) }
            authViewModel.infoMessage?.let { AuthMessage(it, isError = false) }
            AuthPrimaryButton(
                text = "Verify and continue",
                loading = authViewModel.isLoading,
                enabled = code.length == 6
            ) { authViewModel.verifyEmail(email, code) }
            Text("The code expires after 15 minutes.")
            TextButton(
                onClick = { authViewModel.resendVerification(email) },
                enabled = !authViewModel.isLoading && resendSeconds == 0
            ) {
                Text(if (resendSeconds == 0) "Send a new code" else "Send again in ${resendSeconds}s")
            }
            TextButton(onClick = {
                navController.navigate("login") {
                    popUpTo("login") { inclusive = true }
                    launchSingleTop = true
                }
            }) { Text("Back to sign in") }
        }
    }
}
