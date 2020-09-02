package ru.loginov.metric.factory.impl.mutator.lab

import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component
import ru.loginov.metric.factory.MetricPartFactory
import ru.loginov.metric.factory.impl.collector.AverageCollectorFactory
import ru.loginov.metric.factory.impl.mutator.AbstractGetFactory
import ru.loginov.metric.factory.impl.mutator.FilterMutatorFactory
import ru.loginov.metric.factory.impl.mutator.LabMutatorFactory

@Component
@Scope("singleton")
class AFactory  : AbstractGetFactory() {

    override val dependencies: Iterable<Class<out MetricPartFactory<*>>> = listOf(LabMutatorFactory::class.java, FilterMutatorFactory::class.java, AverageCollectorFactory::class.java)

    override val keyTag: String = "CIE_LAB_A"
    override val metricName: String = "Metrics by \"a\" attribute in CIE Lab colors space"
    override val index: Int = 1
}