package ru.loginov.figure

import ru.loginov.color.ColorSettings
import ru.loginov.color.CustomColorSpace

interface Figure<T> {
    val colorSettings: ColorSettings<T>
    val colorSpace: CustomColorSpace<T>

    fun containsPoint(x: Int, y: Int) : Boolean
    fun addPoint(x: Int, y: Int, color: T) : Boolean
    fun getPoints() : Map<Pair<Int, Int>, T>
    fun canBeMesh(figure: Figure<T>) : Boolean
    fun mesh(figure: Figure<T>) : Figure<T>

}