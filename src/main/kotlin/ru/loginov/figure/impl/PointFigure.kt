package ru.loginov.figure.impl

import ru.loginov.color.ColorSettings
import ru.loginov.color.CustomColorSpace
import ru.loginov.figure.Figure
import java.util.*

class PointFigure<T>(override val colorSettings: ColorSettings<T>, override val colorSpace: CustomColorSpace<T>) : Figure<T> {
    private val points: MutableMap<Int, MutableMap<Int, T>> = TreeMap()

    override fun containsPoint(x: Int, y: Int): Boolean {
        return points[x]?.get(y) != null
    }

    override fun addPoint(x: Int, y: Int, color: T): Boolean {
        if (containsPoint(x, y)) {
            return true;
        }

        var addToFigure = false;
        if (points.isEmpty()) {
            points.computeIfAbsent(x) {TreeMap()}[y] = color
            return true;
        }

        for (_x in x - 1 until x + 2) {

            if (addToFigure) {
                break;
            }

            for (_y in y - 1 until y + 2) {
                if ((_x != x || _y != y)) {
                    var tmpColor = points[_x]?.get(_y)
                    addToFigure = tmpColor != null && colorSettings.equalsColors(color, tmpColor)
                    if (addToFigure) {
                        break;
                    }
                }
            }
        }

        if (addToFigure) {
            points.computeIfAbsent(x) {TreeMap()}[y] = color
        }

        return addToFigure;
    }

    override fun getPoints(): Map<Pair<Int, Int>, T> = this.points.flatMap {
        val list: MutableList<Pair<Pair<Int, Int>, T>> = ArrayList()

        it.value.forEach { it2 ->
            list.add(it.key to it2.key to it2.value)
        }

        list
    }.toMap()

    override fun canBeMesh(figure: Figure<T>): Boolean {
        if (figure !is PointFigure) {
            return false;
        }

        this.points.forEach {
            it.value.forEach { tmpIt ->
                val x = it.key
                val y = tmpIt.key

                for (_x in x - 1 until x + 2) {
                    for (_y in y - 1 until y + 2) {
                        val figureColor = figure.points[_x]?.get(y)
                        if (figureColor != null && colorSettings.equalsColors(figureColor, tmpIt.value)) {
                            return true;
                        }
                    }
                }
            }

        }

        return false;
    }

    override fun mesh(figure: Figure<T>): Figure<T> {
        val result = PointFigure(colorSettings, colorSpace)

        result.points.putAll(this.points)
        (figure as PointFigure).points.forEach {
            result.points.computeIfAbsent(it.key) {TreeMap()}.putAll(it.value)
        }
        return result
    }
}