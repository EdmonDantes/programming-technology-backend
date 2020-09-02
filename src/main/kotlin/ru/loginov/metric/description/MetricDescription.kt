package ru.loginov.metric.description

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import ru.loginov.metric.factory.MetricPartFactory

@JsonIgnoreProperties(value = ["factoryClass"])
interface MetricDescription {

    val tag: String

    val name: String?
        get() = null

    val description: String?
        get() = null

    val isCollector: Boolean
        get() = false

    val factoryClass: Class<out MetricPartFactory<*>>
}