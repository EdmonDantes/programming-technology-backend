package ru.loginov.color

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

class XYColorSpace(val Y: Double) {

    private val logger: Logger = LogManager.getLogger()

    fun fromChromaticCoordinates(x: Double, y: Double): DoubleArray {
        val _x = x * (Y / y)
        val z = (1.0 - x - y) * (Y / y)
        logger.trace("Convert chromatic coordinates ({};{}) to XYZ coodinates ({};{};{})", x, y, _x, Y, z)
        return doubleArrayOf(_x, Y, z)
    }

    fun fromChromaticCoordinates(pair: Pair<Double, Double>) = fromChromaticCoordinates(pair.first, pair.second)

    fun toChromaticCoordinates(x: Double, y: Double, z: Double) : DoubleArray = doubleArrayOf(x / (x + y + z), y / (x + y + z), z / (x + y + z))
    fun toChromaticCoordinates(xyz: DoubleArray) = toChromaticCoordinates(xyz[0], xyz[1], xyz[2])
}