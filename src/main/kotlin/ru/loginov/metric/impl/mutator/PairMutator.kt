package ru.loginov.metric.impl.mutator

import ru.loginov.metric.MetricPipeline
import ru.loginov.metric.impl.AbstractMutatorMetricPipeline

class PairMutator(val firstMutator: MetricPipeline?, val secondMutator: MetricPipeline?)  : AbstractMutatorMetricPipeline() {

    override fun mutate(input: List<*>?, index: Int): List<*>? {
        if (firstMutator == null && secondMutator == null) {
            return input
        }

        val list = input?.toMutableList()

        return list?.let {
            var i = index
            while (i < it.size) {
                val pair = input[i]
                if (pair is Pair<*, *>) {
                    it[i] = pair.first?.let { first ->
                        firstMutator?.let {
                            if (first is List<*>)
                                it.onNext(first)
                            else
                                it.onNext(listOf(first))
                        } ?: first
                    } to pair.second?.let { second ->
                        secondMutator?.let {
                            if (second is List<*>)
                                it.onNext(second)
                            else
                                it.onNext(listOf(second))
                        } ?: second
                    }
                }
                i++
            }

            input
        }
    }
}