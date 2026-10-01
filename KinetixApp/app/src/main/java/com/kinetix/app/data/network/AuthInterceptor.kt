package com.kinetix.app.data.network

import com.kinetix.app.data.AuthSession
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor : Interceptor {

    override fun intercept(
        chain: Interceptor.Chain
    ): Response {
        val originalRequest = chain.request()
        val currentToken = AuthSession.current?.token

        val requestBuilder = originalRequest
            .newBuilder()
            .header(
                "Accept",
                "application/json"
            )

        if (!currentToken.isNullOrBlank()) {
            requestBuilder.header(
                "Authorization",
                "Bearer $currentToken"
            )
        }

        return chain.proceed(
            requestBuilder.build()
        )
    }
}