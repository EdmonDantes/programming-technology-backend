
import org.junit.Assert
import ru.loginov.color.impl.LabColorSettings
import ru.loginov.color.impl.lab.LabColorSpace
import ru.loginov.figure.impl.PointFigure
import ru.loginov.figure.impl.RangeFigure
import java.awt.Color
import kotlin.system.measureNanoTime

fun main() {
    val colorSpace = LabColorSpace();
    val settings = LabColorSettings(0.03f, 0.2f, colorSpace)

    val pointFigure0 = PointFigure(settings, colorSpace)
    val pointFigure1 = PointFigure(settings, colorSpace)
    val rangeFigure0 = RangeFigure(settings, colorSpace)
    val rangeFigure1 = RangeFigure(settings, colorSpace)

    val color = colorSpace.fromCustom(Color.BLACK)
    val first = arrayOf(1 to 1, 2 to 2, 3 to 3, 3 to 4, 3 to 5)
    val second = arrayOf(1 to 2, 2 to 2, 3 to 3, 3 to 2, 3 to 1)

    first.forEach {
        val pointTime = measureNanoTime {
            Assert.assertTrue(pointFigure0.addPoint(it.first, it.second, color))
        }

        val rangeTime = measureNanoTime {
            Assert.assertTrue(rangeFigure0.addPoint(it.first, it.second, color))
        }

        println("Point (${it.first};${it.second}). Point time = $pointTime; Range time = $rangeTime")
    }

    println()

    second.forEach {
        val pointTime = measureNanoTime {
            Assert.assertTrue(pointFigure1.addPoint(it.first, it.second, color))
        }

        val rangeTime = measureNanoTime {
            Assert.assertTrue(rangeFigure1.addPoint(it.first, it.second, color))
        }

        println("Point (${it.first};${it.second}). Point time = $pointTime; Range time = $rangeTime")
    }

    Assert.assertTrue(pointFigure0.canBeMesh(pointFigure1))
    Assert.assertTrue(rangeFigure0.canBeMesh(rangeFigure1))


    val resultPoint = pointFigure0.mesh(pointFigure1)

    first.forEach {
        Assert.assertTrue(resultPoint.containsPoint(it.first, it.second))
    }

    second.forEach {
        Assert.assertTrue(resultPoint.containsPoint(it.first, it.second))
    }

}