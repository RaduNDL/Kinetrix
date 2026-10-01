package com.kinetix.app.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kinetix.app.data.AuthSession
import com.kinetix.app.data.models.*
import com.kinetix.app.data.network.RetrofitClient
import com.kinetix.app.data.repository.ApiException
import com.kinetix.app.data.repository.AuthRepository
import kotlinx.coroutines.launch
import java.net.SocketTimeoutException
import java.io.IOException
import java.util.Locale

class AuthViewModel : ViewModel() {
    private val repository = AuthRepository(RetrofitClient.apiService)

    var isLoading by mutableStateOf(false)
        private set
    var errorMessage by mutableStateOf<String?>(null)
        private set
    var infoMessage by mutableStateOf<String?>(null)
        private set
    var isSuccess by mutableStateOf(false)
        private set
    var verificationEmail by mutableStateOf<String?>(null)
        private set
    var resendVersion by mutableStateOf(0)
        private set

    fun register(request: RegisterRequest) {
        val email = normalizeEmail(request.email)
        if (!validateEmail(email)) return
        if (request.password.length < 10 || request.password.toByteArray(Charsets.UTF_8).size > 72) {
            errorMessage = "Use at least 10 characters and at most 72 UTF-8 bytes for your password."
            return
        }
        submit(email, navigateOnEmailFailure = true) {
            repository.register(request.copy(email = email)).map {
                infoMessage = it.message
                verificationEmail = it.email
            }
        }
    }

    fun login(request: LoginRequest) {
        val email = normalizeEmail(request.email)
        if (!validateEmail(email)) return
        if (request.password.isBlank()) {
            errorMessage = "Enter your password."
            return
        }
        submit(email) {
            repository.login(request.copy(email = email)).map {
                AuthSession.start(it)
                isSuccess = true
            }
        }
    }

    fun verifyEmail(email: String, code: String) {
        if (!code.matches(Regex("[0-9]{6}"))) {
            errorMessage = "Enter the six-digit code from your email."
            return
        }
        submit(normalizeEmail(email)) {
            repository.verifyEmail(EmailVerificationRequest(normalizeEmail(email), code)).map {
                AuthSession.start(it)
                isSuccess = true
            }
        }
    }

    fun resendVerification(email: String) {
        val normalized = normalizeEmail(email)
        if (!validateEmail(normalized)) return
        submit(normalized) {
            repository.resendVerification(ResendVerificationRequest(normalized)).map {
                infoMessage = it.message
                resendVersion++
            }
        }
    }

    private fun submit(
        email: String,
        navigateOnEmailFailure: Boolean = false,
        operation: suspend () -> Result<Unit>
    ) {
        if (isLoading) return
        isLoading = true
        clearMessages()
        viewModelScope.launch {
            try {
                operation().onFailure { exception ->
                    errorMessage = when (exception) {
                        is ApiException -> exception.message
                        is SocketTimeoutException -> "Kinetix took too long to respond. Please try again."
                        is IOException -> "Can't connect to Kinetix. Check your connection and try again."
                        else -> "Kinetix returned an unexpected response. Please try again."
                    }
                    if (exception is ApiException &&
                        (exception.code == "email_verification_required" ||
                            (navigateOnEmailFailure && exception.code == "email_delivery_failed"))) {
                        verificationEmail = email
                    }
                }
            } finally {
                isLoading = false
            }
        }
    }

    fun clearMessages() {
        errorMessage = null
        infoMessage = null
    }

    fun resetState() {
        clearMessages()
        isSuccess = false
        verificationEmail = null
    }

    private fun validateEmail(email: String): Boolean {
        val valid = android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
        if (!valid) errorMessage = "Enter a valid email address."
        return valid
    }

    private fun normalizeEmail(email: String) = email.trim().lowercase(Locale.ROOT)
}
