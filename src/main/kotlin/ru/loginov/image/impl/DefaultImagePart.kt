package ru.loginov.image.impl

import ru.loginov.figure.Figure
import ru.loginov.image.ImageManager
import ru.loginov.image.ImagePart
import kotlin.math.min

class DefaultImagePart<T>(override val x: Int, override val y: Int, override val width: Int, override val height: Int, override val manager: ImageManager<T>) :
    ImagePart<T> {

    private val figures: MutableList<Figure<T>> = ArrayList()
    private var sortFigures = false

    override fun addPoint(x: Int, y: Int, color: T) {

        if (manager.backgroundColors.any { manager.colorSettings.equalsColors(it, color) }) {
            return
        }

        if (!figures.any { it.addPoint(x, y, color) }) {
            val figure = manager.createFigure()
            figure.addPoint(x, y, color)
            figures.add(figure)
        }
        sortFigures = false
    }

    override fun getFigures(): List<Figure<T>> {

        if (sortFigures) {
            return figures
        }

        var i = 0
        var j = 0
        while (i < figures.size) {
            if (j >= figures.size) {
                i++
                j = i + 1
                continue
            }

            if (i == j) {
                j++
                continue
            }

            if (figures[i].canBeMesh(figures[j])) {
                var tmp = figures[i].mesh(figures[j])
                figures.removeAt(i)
                figures.removeAt(if (i > j) j else j - 1)
                if (i > j) {
                    i--
                }
                j = i + 1
                figures.add(tmp)
                continue
            }

            j++
        }

        sortFigures = true

        return figures
    }

    override fun mesh(imagePart: ImagePart<T>): ImagePart<T>? {
        var part: DefaultImagePart<T>? = null
        if ((x == imagePart.x + imagePart.width || x + width == imagePart.x) && height == imagePart.height) {
            part = DefaultImagePart(min(x, imagePart.x), min(y, imagePart.y), width + imagePart.width, height, manager)
            part.figures.addAll(this.getFigures())
            part.figures.addAll(imagePart.getFigures())
        } else if ((y == imagePart.y + imagePart.height || y + height == imagePart.y) && width == imagePart.width) {
            part = DefaultImagePart(min(x, imagePart.x), min(y, imagePart.y), width, height + imagePart.height, manager)
            part.figures.addAll(this.getFigures())
            part.figures.addAll(imagePart.getFigures())
        }

        return part
    }
}