package ru.loginov.metric.description.impl.mutator.lab

import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component
import ru.loginov.metric.description.MetricDescription
import ru.loginov.metric.factory.MetricPartFactory
import ru.loginov.metric.factory.impl.mutator.lab.LFactory

@Component
@Scope("singleton")
class LDescription : MetricDescription {

    override val tag: String = "cie_lab_l"

    override val name: String? = "L attribute"

    override val description: String? = "Measures metric values from \"L\" attribute in CIE Lab color space"

    override val factoryClass: Class<out MetricPartFactory<*>> = LFactory::class.java
}