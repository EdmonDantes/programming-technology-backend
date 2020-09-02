package ru.loginov.metric.impl.collector

import ru.loginov.metric.impl.AbstractCollectorMetricPipeline

class FirstCollector : AbstractCollectorMetricPipeline() {
    override fun collect(buffer: List<List<*>>): List<*>? = if (buffer.isNotEmpty()) buffer.first() else null

    override fun name(): String? {
        when {
            parents.isEmpty() -> {
                return null
            }
            parents.size == 1 -> {
                return parents.first()?.getName()
            }
            else -> {
                val builder = StringBuilder()

                parents.forEach {
                    if (builder.isNotEmpty()) {
                        builder.append(";")
                    }

                    it?.getName()?.also {
                        builder.append("\"")
                        builder.append(it)
                        builder.append("\"")
                    }
                }

                return if (builder.isEmpty()) null else "First of [$builder]"
            }
        }
    }
}