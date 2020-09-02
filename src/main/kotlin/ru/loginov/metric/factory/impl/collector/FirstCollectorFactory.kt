package ru.loginov.metric.factory.impl.collector

import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component
import ru.loginov.database.StoreGraph
import ru.loginov.metric.factory.MetricPartFactory
import ru.loginov.metric.impl.collector.FirstCollector

@Component
@Scope("singleton")
class FirstCollectorFactory : MetricPartFactory<FirstCollector> {
    override fun create(graph: StoreGraph, e: Double?, dependencyArgs: Array<*>): FirstCollector = FirstCollector()
}