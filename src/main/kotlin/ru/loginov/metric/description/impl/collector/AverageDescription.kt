package ru.loginov.metric.description.impl.collector

import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component
import ru.loginov.metric.description.MetricDescription
import ru.loginov.metric.factory.MetricPartFactory
import ru.loginov.metric.factory.impl.collector.AverageCollectorFactory

@Component
@Scope("singleton")
class AverageDescription : MetricDescription {

    override val tag: String = "collector_average"
    override val name: String? = "Average"
    override val description: String? = "Measures average value"
    override val isCollector: Boolean = true
    override val factoryClass: Class<out MetricPartFactory<*>> = AverageCollectorFactory::class.java
}