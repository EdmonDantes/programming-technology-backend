package ru.loginov.metric

interface MetricPipeline {

    fun addChild(child: MetricPipeline)

    fun addParent(parent: MetricPipeline)

    fun onNext(input: List<*>?, index: Int = 0) : List<*>?

    fun finish()

    fun getResult() : List<*>?

    fun getNamedResults() : Map<String, *>?

    fun clearResult(): Boolean

    fun getName(): String? = null
}