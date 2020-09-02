package ru.loginov.color

interface ColorSettings<T> {
    val colorSpace: CustomColorSpace<T>

    fun getMaxDifferentFromColors() : Float
    fun getMinPercentForBackgroundColor() : Float
    fun equalsColors(colorA: T, colorB: T) : Boolean = colorSpace.difference(colorA, colorB) < getMaxDifferentFromColors()
    fun notEqualsColors(colorA: T, colorB: T) : Boolean = !equalsColors(colorA, colorB)
    fun isBackground(countPixels: Int, countColor: Int) : Boolean = countColor > countPixels * getMinPercentForBackgroundColor()
}