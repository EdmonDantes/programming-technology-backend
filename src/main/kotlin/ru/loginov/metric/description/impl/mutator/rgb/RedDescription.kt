package ru.loginov.metric.description.impl.mutator.rgb

import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component
import ru.loginov.metric.description.MetricDescription
import ru.loginov.metric.factory.MetricPartFactory
import ru.loginov.metric.factory.impl.mutator.rgb.RedFactory

@Component
@Scope("singleton")
class RedDescription : MetricDescription {

    override val tag: String = "rgb_red"

    override val name: String? = "Red color"

    override val description: String? = "Measures metric values from red color in RGB color space"

    override val factoryClass: Class<out MetricPartFactory<*>> = RedFactory::class.java
}