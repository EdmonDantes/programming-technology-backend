package ru.loginov.metric.factory.impl.saver

import ru.loginov.database.StoreGraph
import ru.loginov.metric.factory.MetricPartFactory
import ru.loginov.metric.impl.saver.MetricSaver
import ru.loginov.metric.manager.pipeline.builder.MetricPipelineBuilder

abstract class AbstractMetricSaverFactory : MetricPartFactory<MetricSaver> {

    protected abstract val keyTag: String

    override fun create(graph: StoreGraph, e: Double?, dependencyArgs: Array<*>): MetricSaver {
        val metricSaver = MetricSaver(graph, keyTag)

        if (dependencyArgs.isNotEmpty() && dependencyArgs[0] != null && dependencyArgs[0] is MetricPipelineBuilder) {
            metricSaver.builder = dependencyArgs[0] as MetricPipelineBuilder
        }

        return metricSaver
    }
}