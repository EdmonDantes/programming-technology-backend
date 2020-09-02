package ru.loginov.color.impl

import ru.loginov.color.ColorSettings
import ru.loginov.color.CustomColorSpace
import ru.loginov.color.impl.lab.LabColor
import kotlin.math.sqrt

class LabColorSettings(private var maxColorDifference: Float, private val minBackgroundPercent: Float, override val colorSpace: CustomColorSpace<LabColor>) : ColorSettings<LabColor> {

    init {
        maxColorDifference *= sqrt(3.0).toFloat()
    }

    override fun getMaxDifferentFromColors(): Float = maxColorDifference

    override fun getMinPercentForBackgroundColor(): Float = minBackgroundPercent
}