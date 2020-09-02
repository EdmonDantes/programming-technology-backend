package ru.loginov.metric.impl.mutator

import ru.loginov.metric.impl.AbstractMutatorMetricPipeline

class FilterMutator(val index: Int) : AbstractMutatorMetricPipeline() {
    override fun mutate(input: List<*>?, index: Int): List<*>? = input?.let { if (it.size > index + this.index) listOf(it[index + this.index]) else null }
}