package com.kinetix.app.data.pose

import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.sqrt

data class PosePoint(
    val x: Float,
    val y: Float,
    val z: Float = 0f,
    val visibility: Float = 1f
)

class PoseAnalyzer {

    fun calculateAngle(
        first: PosePoint,
        middle: PosePoint,
        last: PosePoint
    ): Double {
        val firstVectorX =
            first.x - middle.x

        val firstVectorY =
            first.y - middle.y

        val lastVectorX =
            last.x - middle.x

        val lastVectorY =
            last.y - middle.y

        val firstAngle = atan2(
            firstVectorY.toDouble(),
            firstVectorX.toDouble()
        )

        val lastAngle = atan2(
            lastVectorY.toDouble(),
            lastVectorX.toDouble()
        )

        var angle = Math.toDegrees(
            abs(firstAngle - lastAngle)
        )

        if (angle > 180.0) {
            angle = 360.0 - angle
        }

        return angle
    }

    fun calculateDistance(
        first: PosePoint,
        second: PosePoint
    ): Double {
        val deltaX =
            (first.x - second.x).toDouble()

        val deltaY =
            (first.y - second.y).toDouble()

        val deltaZ =
            (first.z - second.z).toDouble()

        return sqrt(
            deltaX * deltaX +
                    deltaY * deltaY +
                    deltaZ * deltaZ
        )
    }

    fun isVisible(
        point: PosePoint,
        minimumVisibility: Float = 0.5f
    ): Boolean {
        return point.visibility >= minimumVisibility
    }
}