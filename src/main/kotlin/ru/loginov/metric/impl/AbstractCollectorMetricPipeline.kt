package ru.loginov.metric.impl

import ru.loginov.metric.AbstractMetricPipeline
import java.util.LinkedList

abstract class AbstractCollectorMetricPipeline : AbstractMetricPipeline() {

    private val buffer = LinkedList<List<*>>()

    override fun onNext(input: List<*>?, index: Int): List<*>? {
        input?.let { buffer.add(it.subList(index, it.size)) }
        return null
    }

    override fun result(): List<*>? {
        return collect(buffer)
    }

    override fun clear(): Boolean {
        buffer.clear()
        return true
    }

    protected abstract fun collect(buffer: List<List<*>>) : List<*>?

}