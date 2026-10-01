package com.kinetix.app.data.repository

import com.kinetix.app.data.models.AuthResponse
import com.kinetix.app.data.models.EmailVerificationRequest
import com.kinetix.app.data.models.LoginRequest
import com.kinetix.app.data.models.RegisterRequest
import com.kinetix.app.data.models.RegisterResponse
import com.kinetix.app.data.models.ResendVerificationRequest
import com.kinetix.app.data.network.KinetixApiService

class AuthRepository(
    private val apiService: KinetixApiService
) {

    suspend fun register(
        request: RegisterRequest
    ): Result<RegisterResponse> {
        return executeRequest {
            apiService.register(request)
        }
    }

    suspend fun verifyEmail(
        request: EmailVerificationRequest
    ): Result<AuthResponse> {
        return executeRequest {
            apiService.verifyEmail(request)
        }
    }

    suspend fun resendVerification(
        request: ResendVerificationRequest
    ): Result<RegisterResponse> {
        return executeRequest {
            apiService.resendVerification(request)
        }
    }

    suspend fun login(
        request: LoginRequest
    ): Result<AuthResponse> {
        return executeRequest {
            apiService.login(request)
        }
    }

    private suspend fun <T> executeRequest(
        request: suspend () -> retrofit2.Response<T>
    ): Result<T> {
        return try {
            val response = request()

            if (response.isSuccessful) {
                val body = response.body()

                if (body != null) {
                    Result.success(body)
                } else {
                    Result.failure(
                        IllegalStateException(
                            "Server returned an empty response."
                        )
                    )
                }
            } else {
                val errorMessage =
                    response.errorBody()
                        ?.string()
                        ?.takeIf { it.isNotBlank() }
                        ?: "Request failed with HTTP ${response.code()}."

                Result.failure(
                    IllegalStateException(errorMessage)
                )
            }
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }
}