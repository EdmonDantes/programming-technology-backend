package ru.loginov.metric.impl.collector

import ru.loginov.metric.impl.AbstractCollectorMetricPipeline

class AverageCollector : AbstractCollectorMetricPipeline() {

    override fun collect(buffer: List<List<*>>): List<*>? {
        var average = 0.0
        var count = 0

        val iterator = buffer.iterator()
        while (iterator.hasNext()) {
            val array = iterator.next()
            array.forEach {

                if (it is Number) {
                    count++
                    average += (it.toDouble() - average) / count
                }
            }
        }

        return listOf(average)
    }

    override fun name(): String? {

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

        return if (builder.isEmpty()) null else "Average of $builder"
    }
}