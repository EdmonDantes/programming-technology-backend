package ru.loginov.metric.description.impl.mutator.lab

import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component
import ru.loginov.metric.description.MetricDescription
import ru.loginov.metric.factory.MetricPartFactory
import ru.loginov.metric.factory.impl.mutator.lab.AFactory

@Component
@Scope("singleton")
class ADescription : MetricDescription {

    override val tag: String = "cie_lab_a"

    override val name: String? = "A attribute"

    override val description: String? = "Measures metric values from \"a\" attribute in CIE Lab color space"

    override val factoryClass: Class<out MetricPartFactory<*>> = AFactory::class.java
}