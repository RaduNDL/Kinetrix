package com.kinetix.app.data.pose

data class ExerciseAnalysisResult(
    val exerciseType: ExerciseType,
    val repetitions: Int = 0,
    val validRepetitions: Int = 0,
    val invalidRepetitions: Int = 0,
    val feedback: String = "Camera analysis is not started.",
    val confidence: Float = 0f,
    val isCorrectForm: Boolean = false
)