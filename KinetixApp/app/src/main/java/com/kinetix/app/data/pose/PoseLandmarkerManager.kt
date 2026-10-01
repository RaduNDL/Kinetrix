package com.kinetix.app.data.pose

class PoseLandmarkerManager {

    private var initialized = false

    fun initialize(): Boolean {
        initialized = true
        return initialized
    }

    fun isInitialized(): Boolean {
        return initialized
    }

    fun close() {
        initialized = false
    }
}