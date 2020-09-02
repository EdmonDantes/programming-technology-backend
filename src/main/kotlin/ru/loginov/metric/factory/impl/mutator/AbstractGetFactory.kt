package ru.loginov.metric.factory.impl.mutator

import ru.loginov.database.StoreGraph
import ru.loginov.metric.factory.MetricPartFactory
import ru.loginov.metric.factory.impl.collector.AverageCollectorFactory
import ru.loginov.metric.impl.mutator.GetMutator

abstract class AbstractGetFactory : MetricPartFactory<GetMutator> {

    abstract val keyTag: String
    abstract val metricName: String
    abstract val index: Int

    override val dependencies: Iterable<Class<out MetricPartFactory<*>>> = arrayListOf(FilterMutatorFactory::class.java, AverageCollectorFactory::class.java)

    override fun create(graph: StoreGraph, e: Double?, dependencyArgs: Array<*>): GetMutator {
        return GetMutator(keyTag, metricName, graph, e)
    }

    override fun getDependenciesArguments(dependencyFactory: MetricPartFactory<*>): Array<Any> {
        if (dependencyFactory is FilterMutatorFactory) {
            return arrayOf(index)
        }
        return emptyArray()
    }
}