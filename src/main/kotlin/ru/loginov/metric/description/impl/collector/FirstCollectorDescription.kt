package ru.loginov.metric.description.impl.collector

import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component
import ru.loginov.metric.description.MetricDescription
import ru.loginov.metric.factory.MetricPartFactory
import ru.loginov.metric.factory.impl.collector.FirstCollectorFactory

@Component
@Scope("singleton")
class FirstCollectorDescription : MetricDescription {
    override val tag: String = "collector_first"
    override val name: String? = "First"
    override val description: String? = "Get only first value"
    override val isCollector: Boolean = true
    override val factoryClass: Class<out MetricPartFactory<*>> = FirstCollectorFactory::class.java

}