package ru.loginov.graph.impl

import ru.loginov.graph.Graph
import kotlin.math.max
import kotlin.math.min

class PointGraph(private val pointsArray: Array<Pair<Double, Double>>) : Graph {
    override var minX: Double = Double.MAX_VALUE
    override var maxX: Double = Double.MIN_VALUE
    override var minY: Double = minX
    override var maxY: Double = maxX

    constructor(points: Map<Double, Double>) : this(points.map { it.key to it.value }.toTypedArray())

    init {
        pointsArray.sortBy { it.first }

        pointsArray.forEach {
            minX = min(it.first, minX)
            maxX = max(it.first, maxX)
            minY = min(it.second, minY)
            maxY = max(it.second, maxY)
        }
    }

    override fun getY(x: Double, e: Double): Double? {
        if (x < minX || x > maxX) {
            return null
        }

        for (i in pointsArray.indices) {
            return if (x > pointsArray[i].first) {
                if (i + 1 < pointsArray.size && x > pointsArray[i + 1].first) {
                    continue
                }
                (pointsArray[i + 1].second - pointsArray[i].second) /
                        (pointsArray[i + 1].first - pointsArray[i].first) *
                        (x - pointsArray[i].first) + pointsArray[i].second

            } else if (x - pointsArray[i].first < e) {
                pointsArray[i].second
            } else {
                break
            }
        }

        return null
    }
}