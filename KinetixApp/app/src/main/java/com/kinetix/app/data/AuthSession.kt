package com.kinetix.app.data

import com.kinetix.app.data.models.AuthResponse

object AuthSession {

    @Volatile
    var current: AuthResponse? = null
        private set

    fun start(response: AuthResponse) {
        current = response
    }

    fun clear() {
        current = null
    }

    fun isAuthenticated(): Boolean {
        return !current?.token.isNullOrBlank()
    }
}