package ru.loginov.metric.impl.mutator

import ru.loginov.database.ChemistryUnit
import ru.loginov.database.StoreGraph
import ru.loginov.graph.Graph
import ru.loginov.graph.impl.PointGraph
import ru.loginov.metric.impl.AbstractMutatorMetricPipeline

class GetMutator(keyTag: String, private val metricName: String, graph: StoreGraph, val e: Double?) : AbstractMutatorMetricPipeline()  {

    var minUnit: ChemistryUnit? = null
    var graph: Graph? = null

    init {
        graph.metricsValues[keyTag]?.also {
            val pair = it.getPoints()
            pair.first?.also {
                minUnit = it
                this.graph = PointGraph(pair.second)
            } ?: throw IllegalStateException("Can not transform metric to minimum unit")
        } ?: throw IllegalArgumentException("Can not find tag '$keyTag' in graph")
    }

    override fun mutate(input: List<*>?, index: Int): List<*>? = input?.let {
        if (it.size > index && it[index] is Number)
            (it[index] as Number).toDouble().let { x ->
                minUnit?.let {unit ->
                    graph?.let { graph ->
                        listOf(unit, (e?.let { e -> graph.getY(x, e) } ?: graph.getY(x)))
                    }
                }
            }
        else null
    }

    override fun getName(): String? {
        return metricName
    }
}