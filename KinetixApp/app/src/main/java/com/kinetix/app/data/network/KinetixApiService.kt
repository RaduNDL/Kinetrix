package com.kinetix.app.data.network

import com.kinetix.app.data.models.AuthResponse
import com.kinetix.app.data.models.LoginRequest
import com.kinetix.app.data.models.RegisterRequest
import com.kinetix.app.data.models.RegisterResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface KinetixApiService {

    @POST("api/auth/register")
    suspend fun register(
        @Body request: RegisterRequest
    ): Response<RegisterResponse>

    @POST("api/auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<AuthResponse>
}