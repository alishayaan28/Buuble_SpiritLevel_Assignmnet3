package com.example.buublespirit_assignmnet3

import android.graphics.PointF
import kotlin.math.sqrt

class LandscapeCoordinates(
    val mDegree: Float = 10f,
    val radius: Float,
    val bRadius: Float
) {

    fun calValue(xAxis: Float, yAxis: Float, portrait: Boolean): PointF {
        val x = xAxis.coerceIn(-mDegree, mDegree)
        val y = yAxis.coerceIn(-mDegree, mDegree)

        val unitX = x / mDegree
        val unitY = y / mDegree

        val effectiveRadius = radius - bRadius
        val transformedX = unitX * sqrt(1 - unitY * unitY / 2)
        val transformedY = unitY * sqrt(1 - unitX * unitX / 2)

        return if (portrait) {
            PointF(
                transformedX * effectiveRadius,
                transformedY * effectiveRadius
            )
        } else {
            PointF(
                transformedY * effectiveRadius,
                -transformedX * effectiveRadius
            )
        }
    }
}