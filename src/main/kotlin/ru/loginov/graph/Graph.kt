package ru.loginov.graph

interface Graph {
    val minX: Double
    val maxX: Double
    val minY: Double
    val maxY: Double

    fun getY(x: Double, e: Double = 0.0001) : Double?
}