package com.kinetix.app.data.models

data class AuthResponse(
    val userId: String,
    val email: String,
    val firstName: String,
    val lastName: String,
    val token: String
)