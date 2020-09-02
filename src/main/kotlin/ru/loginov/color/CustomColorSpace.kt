package ru.loginov.color

import java.awt.Color

interface CustomColorSpace<T> {

    fun toColor(color: T): Color
    fun fromCustom(color: Color) : T
    fun fromRGB(rgb: Int): T
    fun fromRGB(r: Int, g: Int, b: Int): T
    fun fromRGB(r: Double, g: Double, b: Double): T
    fun difference(colorA: T, colorB: T) : Float
    fun average(colorA: T, colorB: T): T
}