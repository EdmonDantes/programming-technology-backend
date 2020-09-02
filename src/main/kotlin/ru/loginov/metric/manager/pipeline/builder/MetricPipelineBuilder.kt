package ru.loginov.metric.manager.pipeline.builder

import ru.loginov.database.StoreGraph
import ru.loginov.metric.MetricPipeline
import ru.loginov.metric.description.MetricDescription
import ru.loginov.metric.factory.MetricPartFactory

interface MetricPipelineBuilder {

    fun addMutator(description: MetricDescription, dependencies: Iterator<Class<out MetricPartFactory<*>>>? = null) : Int?

    fun addCollector(description: MetricDescription, collectIds: Collection<Int>? = null) : Int?

    fun addSaver(description: MetricDescription) : Int?

    fun build(graph: StoreGraph, e: Double? = null, dependencyArgs: Array<*> = emptyArray<Any>()) : MetricPipeline
}