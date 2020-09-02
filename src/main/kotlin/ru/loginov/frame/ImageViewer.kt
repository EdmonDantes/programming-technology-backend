package ru.loginov.frame

import ru.loginov.image.ImageManager
import java.awt.image.BufferedImage
import java.io.IOException
import java.io.OutputStream

interface ImageViewer<T> {
    var manager: ImageManager<T>?
    var image: BufferedImage?

    fun onShow()

    @Throws(IOException::class)
    fun onSave(output: OutputStream)

    fun onClick(x: Int, y: Int)

}