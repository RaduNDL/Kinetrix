package com.kinetix.app.data.models

data class RegisterRequest(
    val email: String,
    val password: String,
    val firstName: String,
    val lastName: String,
    val dateOfBirth: String,
    val gender: String,
    val heightCm: Double
)

data class RegisterResponse(
    val email: String,
    val message: String
)