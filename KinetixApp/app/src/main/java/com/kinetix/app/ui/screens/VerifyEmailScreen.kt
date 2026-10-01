package com.kinetix.app.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pin
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.kinetix.app.ui.components.AuthBackground
import com.kinetix.app.ui.components.AuthField
import com.kinetix.app.ui.components.AuthMessage
import com.kinetix.app.ui.components.AuthPrimaryButton
import com.kinetix.app.ui.components.AuthTitle
import com.kinetix.app.ui.components.KinetixBrand
import com.kinetix.app.ui.theme.KinetixMuted
import com.kinetix.app.ui.theme.KinetixSurface
import com.kinetix.app.ui.viewmodels.AuthViewModel

@Composable
fun VerifyEmailScreen(
    navController: NavController,
    email: String,
    authViewModel: AuthViewModel = viewModel()
) {
    var code by remember { mutableStateOf("") }

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
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            KinetixBrand()
            Spacer(Modifier.height(32.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(26.dp),
                colors = CardDefaults.cardColors(containerColor = KinetixSurface.copy(alpha = 0.97f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
            ) {
                Column(Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "ONE LAST STEP",
                        color = MaterialTheme.colorScheme.secondary,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.6.sp
                    )
                    AuthTitle("Check your inbox", "Enter the 6-digit code sent to $email. It expires in 15 minutes.")
                    Spacer(Modifier.height(14.dp))
                    AuthField(
                        value = code,
                        onValueChange = { code = it.filter(Char::isDigit).take(6) },
                        label = "Verification code",
                        leadingIcon = Icons.Filled.Pin,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    authViewModel.errorMessage?.let {
                        Spacer(Modifier.height(6.dp))
                        AuthMessage(it, isError = true)
                    }
                    authViewModel.infoMessage?.let {
                        Spacer(Modifier.height(6.dp))
                        AuthMessage(it, isError = false)
                    }
                    Spacer(Modifier.height(10.dp))
                    AuthPrimaryButton("Verify and continue", authViewModel.isLoading, code.length == 6) {
                        authViewModel.verifyEmail(email, code)
                    }
                    Spacer(Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Didn't receive it? ", color = KinetixMuted, style = MaterialTheme.typography.bodyMedium)
                        Text(
                            "Send a new code",
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable(enabled = !authViewModel.isLoading) {
                                authViewModel.resendVerification(email)
                            }
                        )
                    }
                    Text(
                        "Check Spam or Promotions if it isn’t in your inbox.",
                        modifier = Modifier.fillMaxWidth(),
                        color = KinetixMuted,
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        "Use a different email",
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                navController.navigate("register") {
                                    popUpTo("register") { inclusive = true }
                                }
                            },
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}