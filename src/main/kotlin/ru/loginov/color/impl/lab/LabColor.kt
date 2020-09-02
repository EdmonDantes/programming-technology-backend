package ru.loginov.color.impl.lab


class LabColor(val components: DoubleArray) {

    val l
        get() = components[0]
    val a
        get() = components[1]
    val b
        get() = components[2]


    override fun toString(): String {
        return "{L=${components[0]};a=${components[1]};b=${components[2]}}"
    }
}