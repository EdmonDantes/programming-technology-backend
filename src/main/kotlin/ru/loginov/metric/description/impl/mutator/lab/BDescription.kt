package ru.loginov.metric.description.impl.mutator.lab

import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component
import ru.loginov.metric.description.MetricDescription
import ru.loginov.metric.factory.MetricPartFactory
import ru.loginov.metric.factory.impl.mutator.lab.BFactory

@Component
@Scope("singleton")
class BDescription : MetricDescription {

    override val tag: String = "cie_lab_b"

    override val name: String? = "B attribute"

    override val description: String? = "Measures metric values from \"b\" attribute in CIE Lab color space"

    override val factoryClass: Class<out MetricPartFactory<*>> = BFactory::class.java
}