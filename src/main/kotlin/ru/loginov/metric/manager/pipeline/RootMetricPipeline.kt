package ru.loginov.metric.manager.pipeline

import ru.loginov.metric.AbstractMetricPipeline

class RootMetricPipeline : AbstractMetricPipeline() {

    override fun onNext(input: List<*>?, index: Int): List<*>? {
        children.forEach {
            it?.onNext(input)
        }
        return null
    }


}