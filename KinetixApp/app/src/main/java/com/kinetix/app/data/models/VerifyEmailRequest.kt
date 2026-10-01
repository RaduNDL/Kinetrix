package com.kinetix.app.data.models

data class EmailVerificationRequest(
    val email: String,
    val code: String
)

data class ResendVerificationRequest(
    val email: String
)