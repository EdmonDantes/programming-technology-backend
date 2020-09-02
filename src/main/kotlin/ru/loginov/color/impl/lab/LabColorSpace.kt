package ru.loginov.color.impl.lab

import org.apache.commons.math3.linear.MatrixUtils
import org.apache.commons.math3.linear.RealMatrix
import org.apache.logging.log4j.LogManager
import org.springframework.stereotype.Component
import ru.loginov.color.CustomColorSpace
import ru.loginov.color.WhitePoint
import ru.loginov.color.XYColorSpace
import java.awt.Color
import java.awt.color.ColorSpace
import java.awt.color.ColorSpace.CS_CIEXYZ
import kotlin.math.absoluteValue
import kotlin.math.pow
import kotlin.math.sqrt

@Component
class LabColorSpace(Y: Double, whitePoint: WhitePoint) : CustomColorSpace<LabColor> {
    private val logger = LogManager.getLogger("LABColorSpace")

    private val xyColorSpace = XYColorSpace(Y)
    private val whitePoint: DoubleArray = xyColorSpace.fromChromaticCoordinates(whitePoint.points)
    private val rgbToXYZMatrix: RealMatrix
    private val xyzToRGBMatrix: RealMatrix

    private inline fun getXYZ(color: Color, rgbColorSpace: ColorSpace) = rgbColorSpace.toCIEXYZ(color.getComponents(FloatArray(4))).map { it.toDouble() }.toDoubleArray()

    init {
        val rgbColorSpace = Color.WHITE.colorSpace;
        val redXYZ = getXYZ(Color(255, 0, 0), rgbColorSpace)
        val greenXYZ = getXYZ(Color(0, 255, 0), rgbColorSpace)
        val blueXYZ = getXYZ(Color(0, 0, 255), rgbColorSpace)
        val whiteXYZ = getXYZ(Color(255,255,255), rgbColorSpace);


        val sMatrix = MatrixUtils.inverse(MatrixUtils.createRealMatrix(arrayOf(
                doubleArrayOf(redXYZ[0], greenXYZ[0], blueXYZ[0]),
                doubleArrayOf(redXYZ[1], greenXYZ[1], blueXYZ[1]),
                doubleArrayOf(redXYZ[2], greenXYZ[2], blueXYZ[2])
        ))).multiply(MatrixUtils.createRealMatrix(arrayOf(
                doubleArrayOf(whiteXYZ[0]),
                doubleArrayOf(whiteXYZ[1]),
                doubleArrayOf(whiteXYZ[2])
        )))

        val s = sMatrix.data

        rgbToXYZMatrix = MatrixUtils.createRealMatrix(arrayOf(
                doubleArrayOf(s[0][0] * redXYZ[0], s[1][0] * greenXYZ[0], s[2][0] * blueXYZ[0]),
                doubleArrayOf(s[0][0] * redXYZ[1], s[1][0] * greenXYZ[1], s[2][0] * blueXYZ[1]),
                doubleArrayOf(s[0][0] * redXYZ[2], s[1][0] * greenXYZ[2], s[2][0] * blueXYZ[2])
        ))

        xyzToRGBMatrix = MatrixUtils.inverse(rgbToXYZMatrix);

        logger.debug("Create lab color space with Y: {} and white point with chromatic coordinates {}}", Y, this.whitePoint)
    }

    constructor() : this(1.0, WhitePoint.D65_2D)
    constructor(whitePoint: WhitePoint) : this(1.0, whitePoint)

    companion object {
        val xyzColorSpace = ColorSpace.getInstance(CS_CIEXYZ)
        val const0 = 6.0 / 29.0
        val const1 = const0.pow(3.0)
        val const2 = (29.0 / 6.0).pow(2.0)
        val const3 = 4.0 / 29.0
        val const4 = 1.0 / 3.0
        val const5 = 16.0 / 116.0
        val const6 = 3 * const0.pow(2)
        val const7 = 1 / 2.4
    }

    override fun toColor(color: LabColor): Color {
        return Color(xyzColorSpace, toCIEXYZ(color.components).map { it.toFloat() }.toFloatArray(), 1.0f)
    }

    override fun fromCustom(color: Color): LabColor {
        return LabColor(fromCIEXYZ(color.getComponents(xyzColorSpace, FloatArray(4)).map { it.toDouble() }.toDoubleArray()))
    }

    //TODO: must be more better
    override fun fromRGB(rgb: Int): LabColor = LabColor(fromCIEXYZ(rgbToXYZ(rgb)))

    //TODO: must be more better
    override fun fromRGB(r: Int, g: Int, b:Int) = LabColor(fromCIEXYZ(rgbToXYZ(r, g, b)))

    //TODO: must be more better
    override fun fromRGB(r: Double, g: Double, b: Double): LabColor = LabColor(fromCIEXYZ(rgbToXYZ(r, g, b)))

    override fun difference(colorA: LabColor, colorB: LabColor): Float {
        val firstComponent = colorA.components[0] - colorB.components[0]
        val secondComponent = colorA.components[1] - colorB.components[1]
        val thirdComponent = colorA.components[2] - colorB.components[2]

        return sqrt(firstComponent * firstComponent + secondComponent * secondComponent + thirdComponent * thirdComponent).toFloat() / 100.0f
    }

    override fun average(colorA: LabColor, colorB: LabColor): LabColor {
        return LabColor(doubleArrayOf(
            (colorA.components[0] + colorB.components[0]) / 2,
            (colorA.components[1] + colorB.components[1]) / 2,
            (colorA.components[2] + colorB.components[2]) / 2
        ))
    }

    private fun toCIEXYZ(colorvalue: DoubleArray): DoubleArray {
        if (colorvalue.size != 3  || colorvalue.any { it < 0.0}) {
            throw IllegalArgumentException("Wrong component's array for convert CIEXYZ to CIELAB")
        }

        val fy = (colorvalue[0] + 16) / 116
        val fx = fy + colorvalue[1] / 500
        val fz = fy - colorvalue[2] / 200
        return doubleArrayOf(
                fToXYZ(fx, whitePoint[0]),
                fToXYZ(fy, whitePoint[1]),
                fToXYZ(fz, whitePoint[2])
        )
    }

    private fun fromCIEXYZ(colorvalue: DoubleArray): DoubleArray {
        if (colorvalue.size < 3 || colorvalue.size > 4 || colorvalue.any {it > 1.0 || it < 0.0}) {
            throw IllegalArgumentException("Wrong component's array for convert CIEXYZ to CIELAB")
        }

        // 0 - L
        // 1 - a
        // 2 - b
        return doubleArrayOf(
                116.0 * fToLAB(colorvalue[1] / whitePoint[1]) - 16.0,
                500.0 * (fToLAB(colorvalue[0] / whitePoint[0]) - fToLAB(colorvalue[1] / whitePoint[1])).absoluteValue,
                200.0 * (fToLAB(colorvalue[1] / whitePoint[1]) - fToLAB(colorvalue[2] / whitePoint[2])).absoluteValue)
    }


    private fun rgbToXYZ(rgb: Int) : DoubleArray {

        val r = rgb.shr(16).and(0xff)
        val g = rgb.shr(8).and(0xff)
        val b = rgb.and(0xff)

        return rgbToXYZ(r,g,b)
    }

    private inline fun rgbToXYZ(r: Int, g:Int, b:Int) : DoubleArray = rgbToXYZMatrix.multiply(MatrixUtils.createColumnRealMatrix(intArrayOf(r,g,b).map { fRGBtoXYZ(it.toDouble() / 255.0) }.toDoubleArray())).getColumn(0)

    private inline fun rgbToXYZ(r: Double, g: Double, b: Double) : DoubleArray = rgbToXYZMatrix.multiply(MatrixUtils.createColumnRealMatrix(doubleArrayOf(r,g,b))).getColumn(0)

    private fun xyzToRGB(xyz: DoubleArray) : DoubleArray {
        val rgbMatrix = xyzToRGBMatrix.multiply(MatrixUtils.createColumnRealMatrix(xyz))
        return rgbMatrix.getColumn(0).map { fXYZtoRGB(it) }.toDoubleArray()
    }


    private inline fun fRGBtoXYZ(v: Double) = if (v <= 0.04045) v / 12.92 else ((v + 0.055) / 1.055).pow(2.4)
    private inline fun fXYZtoRGB(u: Double) = if (u <= 0.0031308) u * 12.92 else 1.055 * u.pow(const7) - 0.055
    private inline fun fToLAB(t: Double) = if (t > const1) t.pow(const4) else const4 * const2 * t + const3
    private inline fun fToXYZ(x: Double, y: Double) = if (x > const0) y * x.pow(3) else (x - const5) * const6 * y

}