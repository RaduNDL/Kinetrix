package com.kinetix.app.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kinetix.app.data.AuthSession
import com.kinetix.app.data.models.LoginRequest
import com.kinetix.app.data.models.RegisterRequest
import com.kinetix.app.data.network.RetrofitClient
import kotlinx.coroutines.launch
import org.json.JSONObject

class AuthViewModel : ViewModel() {

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    var infoMessage by mutableStateOf<String?>(null)
        private set

    var isSuccess by mutableStateOf(false)
        private set

    var isRegistrationCompleted by mutableStateOf(false)
        private set

    fun register(request: RegisterRequest) {
        val normalizedEmail = request.email
            .trim()
            .lowercase()

        if (
            !android.util.Patterns.EMAIL_ADDRESS
                .matcher(normalizedEmail)
                .matches()
        ) {
            errorMessage = "Enter a valid email address."
            return
        }

        if (request.password.length < 10) {
            errorMessage = "Use a password with at least 10 characters."
            return
        }

        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            infoMessage = null

            try {
                val response = RetrofitClient
                    .apiService
                    .register(
                        request.copy(
                            email = normalizedEmail
                        )
                    )

                if (
                    response.isSuccessful &&
                    response.body() != null
                ) {
                    infoMessage = response.body()?.message
                        ?: "Account created successfully."

                    isRegistrationCompleted = true
                } else {
                    errorMessage = readServerMessage(
                        raw = response.errorBody()?.string(),
                        fallback = "Registration could not be completed."
                    )
                }
            } catch (_: Exception) {
                errorMessage =
                    "Can't reach Kinetix. Check that the backend is running."
            } finally {
                isLoading = false
            }
        }
    }

    fun login(request: LoginRequest) {
        val normalizedEmail = request.email
            .trim()
            .lowercase()

        if (
            !android.util.Patterns.EMAIL_ADDRESS
                .matcher(normalizedEmail)
                .matches()
        ) {
            errorMessage = "Enter a valid email address."
            return
        }

        if (request.password.isBlank()) {
            errorMessage = "Enter your password."
            return
        }

        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            infoMessage = null

            try {
                val response = RetrofitClient
                    .apiService
                    .login(
                        request.copy(
                            email = normalizedEmail
                        )
                    )

                if (
                    response.isSuccessful &&
                    response.body() != null
                ) {
                    AuthSession.start(response.body()!!)
                    isSuccess = true
                } else {
                    errorMessage = readServerMessage(
                        raw = response.errorBody()?.string(),
                        fallback = "Invalid email or password."
                    )
                }
            } catch (_: Exception) {
                errorMessage =
                    "Can't reach Kinetix. Check that the backend is running."
            } finally {
                isLoading = false
            }
        }
    }

    fun resetState() {
        errorMessage = null
        infoMessage = null
        isSuccess = false
        isRegistrationCompleted = false
    }

    private fun readServerMessage(
        raw: String?,
        fallback: String
    ): String {
        if (raw.isNullOrBlank()) {
            return fallback
        }

        return try {
            val json = JSONObject(raw)

            json.optString("message")
                .takeIf { it.isNotBlank() }
                ?: json.optString("detail")
                    .takeIf { it.isNotBlank() }
                ?: json.optString("title")
                    .takeIf { it.isNotBlank() }
                ?: fallback
        } catch (_: Exception) {
            fallback
        }
    }
}