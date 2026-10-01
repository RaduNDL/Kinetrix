package com.kinetix.app.data.network

import com.kinetix.app.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    private val loggingInterceptor =
        HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BASIC
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }

    private val okHttpClient =
        OkHttpClient.Builder()
            .connectTimeout(
                15,
                TimeUnit.SECONDS
            )
            .readTimeout(
                30,
                TimeUnit.SECONDS
            )
            .writeTimeout(
                30,
                TimeUnit.SECONDS
            )
            .addInterceptor(AuthInterceptor())
            .addInterceptor(loggingInterceptor)
            .build()

    val apiService: KinetixApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(
                GsonConverterFactory.create()
            )
            .build()
            .create(KinetixApiService::class.java)
    }
}