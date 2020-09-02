package ru.loginov.metric.description.impl.mutator.rgb

import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component
import ru.loginov.metric.description.MetricDescription
import ru.loginov.metric.factory.MetricPartFactory
import ru.loginov.metric.factory.impl.mutator.rgb.BlueFactory

@Component
@Scope("singleton")
class BlueDescription : MetricDescription {

    override val tag: String = "rgb_blue"

    override val name: String? = "Blue color"

    override val description: String? = "Measures metric values from blue color in RGB color space"

    override val factoryClass: Class<out MetricPartFactory<*>> = BlueFactory::class.java

}