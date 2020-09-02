package ru.loginov.metric.impl

import ru.loginov.metric.AbstractMetricPipeline

abstract class AbstractMutatorMetricPipeline : AbstractMetricPipeline()  {

    override fun onNext(input: List<*>?, index: Int): List<*>? {
        val result = mutate(input, index)
        return result?.also { output -> children.forEach { it?.onNext(output, 0) } }
    }

    protected abstract fun mutate(input: List<*>?, index: Int): List<*>?


}