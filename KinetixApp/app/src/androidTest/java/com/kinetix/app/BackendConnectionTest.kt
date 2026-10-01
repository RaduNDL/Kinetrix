package com.kinetix.app

import androidx.test.ext.junit.runners.AndroidJUnit4
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.TimeUnit

/** Run on a connected debug device after Start-Kinetix.ps1. No accounts or emails are created. */
@RunWith(AndroidJUnit4::class)
class BackendConnectionTest {
    private val client = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS).readTimeout(10, TimeUnit.SECONDS).build()

    @Test
    fun deviceCanReachApiAndDatabase() {
        client.newCall(Request.Builder().url(BuildConfig.BASE_URL + "api/health").build())
            .execute().use { response ->
                assertEquals(200, response.code)
                val body = JSONObject(response.body!!.string())
                assertEquals("ok", body.getString("api"))
                assertEquals("ok", body.getString("database"))
            }
    }

    @Test
    fun deviceReceivesLoginErrorsFromBackend() {
        val json = """{"email":"connection-test@example.invalid","password":"NoRealAccount123!"}"""
        val request = Request.Builder().url(BuildConfig.BASE_URL + "api/auth/login")
            .post(json.toRequestBody("application/json".toMediaType())).build()
        client.newCall(request).execute().use { response ->
            assertEquals(401, response.code)
            assertEquals("Invalid email or password.", JSONObject(response.body!!.string()).getString("message"))
        }
    }
}
