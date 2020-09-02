package ru.loginov.figure.impl

import ru.loginov.color.ColorSettings
import ru.loginov.color.CustomColorSpace
import ru.loginov.figure.Figure
import java.util.*
import kotlin.collections.ArrayList

class RangeFigure<T>(override val colorSettings: ColorSettings<T>, override val colorSpace: CustomColorSpace<T>) : Figure<T> {

    private val points: MutableMap<Int, MutableList<Pair<Pair<Int,T>, Pair<Int, T>>>> = TreeMap()

    override fun containsPoint(x: Int, y: Int): Boolean {
        return points[x]?.any { y >= it.first.first && y <= it.second.first } ?: false
    }

    override fun addPoint(x: Int, y: Int, color: T): Boolean {
        if (points.isEmpty()) {
            points[x] = arrayListOf((y to color) to (y to color))
            return true
        }

        for(_x in x - 1 until x + 2) {
            for (_y in y - 1 until y + 2) {
                if (containsPoint(_x, _y)) {
                    val tmp = points[_x]?.filter { _y in it.first.first..it.second.first && colorSettings.equalsColors(color, it.second.second) }
                    if (tmp != null && tmp.isNotEmpty()) {
                        val filtered = points.computeIfAbsent(x) {ArrayList()}.filter { y in it.first.first..it.second.first }
                        when (filtered.size) {
                            0 -> points[x]!!.add((y to color) to (y to color))
                            else -> {
                                points[x]!!.removeAll(filtered)
                                points[x]!!.add(merge(tmp + ((y to color) to (y to color))))
                            }
                        }
                        return true
                    }
                }
            }
        }
        return false
    }

    override fun getPoints(): Map<Pair<Int, Int>, T> {
         return points.flatMap {
             val list: MutableList<Pair<Pair<Int, Int>, T>> = ArrayList()
             it.value.forEach { pair ->
                 for (i in pair.first.first until pair.second.first) {
                     list.add(it.key to i to pair.first.second)
                 }
             }
             list
        }.toMap()
    }

    override fun canBeMesh(figure: Figure<T>): Boolean {
        return figure is RangeFigure && figure.points.any {
            var result = false
            for (x in it.key - 1 until it.key + 1) {
                result = points[x]?.any { thisRange ->
                    it.value.any {figureRange ->
                        canMerge(thisRange.first.first, thisRange.second.first, figureRange.first.first, figureRange.second.first)
                                && colorSettings.equalsColors(colorSpace.average(thisRange.first.second, thisRange.second.second), colorSpace.average(figureRange.first.second, figureRange.second.second))
                    }
                } ?: false
                if (result) {
                    break
                }
            }

            result
        }
    }

    override fun mesh(figure: Figure<T>): Figure<T> {
        TODO("implement")
    }

    private fun canMerge(aStart: Int, aEnd: Int, bStart: Int, bEnd: Int) = aStart in bStart..bEnd || aEnd in bStart..bEnd

    private fun canMerge(a: IntRange, b: IntRange) = canMerge(a.first, a.last, b.first, b.last)

    private fun merge(list: List<Pair<Pair<Int, T>, Pair<Int, T>>>): Pair<Pair<Int, T>, Pair<Int, T>> {
        var top: Pair<Int, T>? = null
        var bottom: Pair<Int, T>? = null

        list.forEach {
            if (top == null) {
                top = it.first
            }

            if (bottom == null) {
                bottom = it.second
            }

            if (it.first.first < top!!.first) {
                top = it.first
            }

            if (it.second.first > bottom!!.first) {
                bottom = it.second
            }
        }

        if (top != null && bottom != null) {
            return top!! to bottom!!
        } else {
            throw IllegalStateException("Can not find pairs")
        }
    }
}