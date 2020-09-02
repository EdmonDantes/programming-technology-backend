package ru.loginov.image

import ru.loginov.color.ColorSettings
import ru.loginov.color.CustomColorSpace
import ru.loginov.figure.Figure

interface ImageManager<T> {

    val maxWidthImagePart: Int
    val maxHeightImagePart: Int
    val colorSpace: CustomColorSpace<T>
    val colorSettings: ColorSettings<T>
    val figureClass: Class<out Figure<T>>
    val backgroundColors: Collection<T>

    fun createFigure(): Figure<T>
    fun init(width: Int, height: Int, pointGetter: (Int, Int) -> Int, progressFunc: (Int, Int) -> Unit)
    fun run(progressFunc: (Int, Int) -> Unit) : ImagePart<T>?

}