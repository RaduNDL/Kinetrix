package com.kinetix.app.data.repository

import com.kinetix.app.data.models.*
import com.kinetix.app.data.network.KinetixApiService
import kotlinx.coroutines.CancellationException
import org.json.JSONObject
import retrofit2.Response

class ApiException(val status: Int, val code: String?, message: String) : Exception(message)

class AuthRepository(private val apiService: KinetixApiService) {
    suspend fun register(request: RegisterRequest) = executeRequest { apiService.register(request) }
    suspend fun login(request: LoginRequest) = executeRequest { apiService.login(request) }
    suspend fun verifyEmail(request: EmailVerificationRequest) = executeRequest { apiService.verifyEmail(request) }
    suspend fun resendVerification(request: ResendVerificationRequest) =
        executeRequest { apiService.resendVerification(request) }

    private suspend fun <T> executeRequest(request: suspend () -> Response<T>): Result<T> {
        return try {
            val response = request()
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) Result.success(body)
                else Result.failure(IllegalStateException("The server returned an empty response."))
            } else {
                val json = runCatching { JSONObject(response.errorBody()?.string().orEmpty()) }.getOrNull()
                val errors = json?.optJSONObject("errors")
                val validationMessage = errors?.keys()?.asSequence()
                    ?.mapNotNull { errors.optJSONArray(it)?.optString(0)?.takeIf(String::isNotBlank) }
                    ?.joinToString(" ")?.takeIf(String::isNotBlank)
                val message = validationMessage
                    ?: json?.optString("message")?.takeIf(String::isNotBlank)
                    ?: json?.optString("detail")?.takeIf(String::isNotBlank)
                    ?: if (response.code() == 429) "Too many requests. Wait one minute and try again."
                    else if (response.code() >= 500) "Kinetix is temporarily unavailable. Please try again."
                    else json?.optString("title")?.takeIf(String::isNotBlank)
                    ?: "Request failed (HTTP ${response.code()})."
                Result.failure(ApiException(response.code(), json?.optString("code"), message))
            }
        } catch (exception: CancellationException) {
            throw exception
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }
}
