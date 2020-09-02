package ru.loginov.metric.impl.saver

import ru.loginov.database.ChemistryValue
import ru.loginov.database.StoreGraph
import ru.loginov.database.StoreMetricValues
import ru.loginov.metric.MetricPipeline
import ru.loginov.metric.manager.pipeline.builder.MetricPipelineBuilder


class MetricSaver(private val graph: StoreGraph, private val keyTag: String) : MetricPipeline {

    lateinit var builder: MetricPipelineBuilder

    val values = StoreMetricValues()

    var pipeline: MetricPipeline? = null

    override fun addChild(child: MetricPipeline) {
        throw UnsupportedOperationException("Can not add child to saver")
    }

    override fun addParent(parent: MetricPipeline) {}

    override fun onNext(input: List<*>?, index: Int) : List<*>? {
        if (pipeline == null) {
            pipeline = buildSubPipeline()
        }

        input?.also {
            pipeline!!.clearResult()
            if (it.size >= index + 2 && it[index] is ChemistryValue) {
                val value = it[index] as ChemistryValue

                var i = index + 1
                while (i < it.size) {
                    it[i++]?.also {
                        pipeline!!.onNext(it as List<*>)
                    }
                }

                pipeline!!.finish()
                val result = pipeline!!.getResult()
                result?.also {
                    if (it.size == 1 && it[0] is Number) {
                        values.values[(it[0] as Number).toDouble()] = value
                    }
                }
            }
        }

        return null
    }

    override fun finish() {
        graph.metricsValues[keyTag] = values
    }

    override fun getResult(): List<*>? = null

    override fun getNamedResults(): Map<String, *>? = emptyMap<String, Any>()

    override fun clearResult(): Boolean = true

    override fun getName(): String? = null

    private fun buildSubPipeline() : MetricPipeline = builder.build(graph, null)
}