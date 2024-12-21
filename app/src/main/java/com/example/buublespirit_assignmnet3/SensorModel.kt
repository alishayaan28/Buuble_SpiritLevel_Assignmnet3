package com.example.buublespirit_assignmnet3

data class SensorModel(
    val landscape: Boolean = false,
    val portraitX: Float = 0f,
    val xVal: List<Float> = emptyList(),
    val yVal: List<Float> = emptyList(),
    val landscapeX: Float = 0f,
    val landscapeY: Float = 0f
)