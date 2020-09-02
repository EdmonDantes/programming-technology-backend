package ru.loginov.metric.factory.impl.saver.rgb

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component
import ru.loginov.metric.factory.MetricPartFactory
import ru.loginov.metric.factory.impl.collector.AverageCollectorFactory
import ru.loginov.metric.factory.impl.mutator.FilterMutatorFactory
import ru.loginov.metric.factory.impl.saver.AbstractMetricSaverFactory

@Component
@Scope("singleton")
@Qualifier("saver")
class BlueSaverFactory  : AbstractMetricSaverFactory() {

    override val dependencies: Iterable<Class<out MetricPartFactory<*>>> = listOf(FilterMutatorFactory::class.java, AverageCollectorFactory::class.java)

    override val keyTag: String = "RGB_BLUE"

    override fun getDependenciesArguments(dependencyFactory: MetricPartFactory<*>): Array<Any> {
        return arrayOf(2)
    }

}