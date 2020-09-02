package ru.loginov.metric.factory

import ru.loginov.database.StoreGraph
import ru.loginov.metric.MetricPipeline
import java.util.Collections

interface MetricPartFactory<T : MetricPipeline> {

    val dependencies: Iterable<Class<out MetricPartFactory<*>>>
        get() = Collections.emptyList()

    fun create(graph: StoreGraph, e: Double?, dependencyArgs: Array<*>) : T

    fun getDependenciesArguments(dependencyFactory: MetricPartFactory<*>) : Array<Any> = emptyArray()

}