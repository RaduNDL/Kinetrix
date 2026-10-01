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
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import android.net.Uri
import com.kinetix.app.data.models.LoginRequest
import com.kinetix.app.ui.components.AuthBackground
import com.kinetix.app.ui.components.AuthField
import com.kinetix.app.ui.components.AuthMessage
import com.kinetix.app.ui.components.AuthPrimaryButton
import com.kinetix.app.ui.components.AuthTitle
import com.kinetix.app.ui.components.FieldGap
import com.kinetix.app.ui.components.KinetixBrand
import com.kinetix.app.ui.theme.KinetixMuted
import com.kinetix.app.ui.theme.KinetixSurface
import com.kinetix.app.ui.viewmodels.AuthViewModel

@Composable
fun LoginScreen(navController: NavController, authViewModel: AuthViewModel = viewModel()) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    LaunchedEffect(authViewModel.verificationEmail) {
        authViewModel.verificationEmail?.let { pendingEmail ->
            val notice = authViewModel.errorMessage.orEmpty()
            authViewModel.resetState()
            navController.navigate("verify-email?email=${Uri.encode(pendingEmail)}&notice=${Uri.encode(notice)}") {
                launchSingleTop = true
            }
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
                colors = CardDefaults.cardColors(containerColor = KinetixSurface.copy(alpha = 0.96f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "WELCOME BACK",
                        color = MaterialTheme.colorScheme.secondary,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.7.sp
                    )
                    Spacer(Modifier.height(5.dp))
                    AuthTitle("Your next rep starts here.", "Sign in to continue your Kinetix journey.")
                    Spacer(Modifier.height(22.dp))

                    AuthField(
                        value = email,
                        onValueChange = { email = it; authViewModel.resetState() },
                        label = "Email address",
                        leadingIcon = Icons.Filled.Email,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                    )
                    FieldGap()
                    AuthField(
                        value = password,
                        onValueChange = { password = it; authViewModel.resetState() },
                        label = "Password",
                        leadingIcon = Icons.Filled.Lock,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                    contentDescription = if (passwordVisible) "Hide password" else "Show password",
                                    tint = KinetixMuted
                                )
                            }
                        }
                    )

                    authViewModel.errorMessage?.let {
                        Spacer(Modifier.height(12.dp))
                        AuthMessage(it, isError = true)
                    }

                    Spacer(Modifier.height(20.dp))
                    AuthPrimaryButton(
                        text = "Sign in",
                        loading = authViewModel.isLoading,
                        enabled = email.isNotBlank() && password.isNotBlank()
                    ) {
                        authViewModel.login(LoginRequest(email, password))
                    }

                    Spacer(Modifier.height(22.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("New to Kinetix? ", color = KinetixMuted, style = MaterialTheme.typography.bodyMedium)
                        Text(
                            "Create account",
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable { navController.navigate("register") }
                        )
                    }
                }
            }
            Spacer(Modifier.height(20.dp))
            Text(
                text = "TRAIN SMARTER · MOVE BETTER",
                color = KinetixMuted,
                style = MaterialTheme.typography.labelSmall,
                letterSpacing = 1.3.sp
            )
        }
    }
}
