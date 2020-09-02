package ru.loginov.image

import ru.loginov.figure.Figure

interface ImagePart<T> {
    val x: Int
    val y: Int
    val width: Int
    val height: Int
    val manager: ImageManager<T>

    fun addPoint(x: Int, y: Int, color: T)
    fun getFigures(): List<Figure<T>>
    fun mesh(imagePart: ImagePart<T>): ImagePart<T>?

}