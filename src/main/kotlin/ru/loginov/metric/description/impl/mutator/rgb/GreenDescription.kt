package ru.loginov.metric.description.impl.mutator.rgb

import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component
import ru.loginov.metric.description.MetricDescription
import ru.loginov.metric.factory.MetricPartFactory
import ru.loginov.metric.factory.impl.mutator.rgb.GreenFactory

@Component
@Scope("singleton")
class GreenDescription : MetricDescription {

    override val tag: String = "rgb_green"

    override val name: String? = "Green color"

    override val description: String? = "Measures metric values from green color in RGB color space"

    override val factoryClass: Class<out MetricPartFactory<*>> = GreenFactory::class.java

}