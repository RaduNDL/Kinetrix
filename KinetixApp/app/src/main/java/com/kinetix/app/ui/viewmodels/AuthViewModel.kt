package com.kinetix.app.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kinetix.app.data.AuthSession
import com.kinetix.app.data.models.EmailVerificationRequest
import com.kinetix.app.data.models.LoginRequest
import com.kinetix.app.data.models.RegisterRequest
import com.kinetix.app.data.models.ResendVerificationRequest
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
    var isRegistrationSubmitted by mutableStateOf(false)
        private set
    var verificationEmail by mutableStateOf<String?>(null)
        private set

    fun register(request: RegisterRequest) {
        val normalizedEmail = request.email.trim().lowercase()
        val domain = normalizedEmail.substringAfterLast('@', "")
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(normalizedEmail).matches()) {
            errorMessage = "Enter a valid email address."
            return
        }
        if (domain != "gmail.com" && domain != "googlemail.com") {
            errorMessage = "Use a Gmail address. We will email you a code to confirm you can access it."
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
                val response = RetrofitClient.apiService.register(request.copy(email = normalizedEmail))
                if (response.isSuccessful && response.body() != null) {
                    verificationEmail = response.body()!!.email.ifBlank { normalizedEmail }
                    isRegistrationSubmitted = true
                } else {
                    errorMessage = readServerMessage(response.errorBody()?.string(), "Registration could not be completed.")
                }
            } catch (_: Exception) {
                errorMessage = "Can't reach Kinetix. Start the backend and run adb reverse tcp:5068 tcp:5068."
            } finally {
                isLoading = false
            }
        }
    }

    fun verifyEmail(email: String, code: String) {
        if (!code.matches(Regex("^\\d{6}$"))) {
            errorMessage = "Enter the 6-digit code from your email."
            return
        }

        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            infoMessage = null
            try {
                val response = RetrofitClient.apiService.verifyEmail(EmailVerificationRequest(email, code))
                if (response.isSuccessful && response.body() != null) {
                    AuthSession.start(response.body()!!)
                    isSuccess = true
                } else {
                    errorMessage = readServerMessage(response.errorBody()?.string(), "That code could not be verified.")
                }
            } catch (_: Exception) {
                errorMessage = "Can't reach Kinetix. Check that the backend is running and adb reverse is active."
            } finally {
                isLoading = false
            }
        }
    }

    fun resendVerification(email: String) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            infoMessage = null
            try {
                val response = RetrofitClient.apiService.resendVerification(ResendVerificationRequest(email))
                if (response.isSuccessful) {
                    infoMessage = response.body()?.message ?: "If this account is pending, a new code was sent."
                } else {
                    errorMessage = readServerMessage(response.errorBody()?.string(), "A new code could not be sent yet.")
                }
            } catch (_: Exception) {
                errorMessage = "Can't reach Kinetix. Check that the backend is running and adb reverse is active."
            } finally {
                isLoading = false
            }
        }
    }

    fun login(request: LoginRequest) {
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(request.email.trim()).matches()) {
            errorMessage = "Enter a valid email address."
            return
        }

        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            infoMessage = null
            try {
                val response = RetrofitClient.apiService.login(request.copy(email = request.email.trim()))
                if (response.isSuccessful && response.body() != null) {
                    AuthSession.start(response.body()!!)
                    isSuccess = true
                } else {
                    errorMessage = readServerMessage(response.errorBody()?.string(), "Invalid email or password.")
                }
            } catch (_: Exception) {
                errorMessage = "Can't reach Kinetix. Start the backend and run adb reverse tcp:5068 tcp:5068."
            } finally {
                isLoading = false
            }
        }
    }

    fun resetState() {
        errorMessage = null
        infoMessage = null
        isSuccess = false
        isRegistrationSubmitted = false
        verificationEmail = null
    }

    private fun readServerMessage(raw: String?, fallback: String): String {
        if (raw.isNullOrBlank()) return fallback
        return try {
            val json = JSONObject(raw)
            json.optString("message").takeIf { it.isNotBlank() }
                ?: json.optString("detail").takeIf { it.isNotBlank() }
                ?: json.optString("title").takeIf { it.isNotBlank() }
                ?: fallback
        } catch (_: Exception) {
            fallback
        }
    }
}



