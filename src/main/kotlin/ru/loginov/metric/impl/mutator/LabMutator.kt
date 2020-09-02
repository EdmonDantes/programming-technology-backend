package ru.loginov.metric.impl.mutator

import ru.loginov.color.impl.lab.LabColorSpace
import ru.loginov.metric.impl.AbstractMutatorMetricPipeline

class LabMutator(val colorSpace: LabColorSpace) : AbstractMutatorMetricPipeline() {

    override fun mutate(input: List<*>?, index: Int): List<*>? = input?.let {
        if (it.size >= index + 3
                && it[index] is Number
                && it[index + 1] is Number
                && it[index + 2] is Number)
            colorSpace.fromRGB(
                    (it[0] as Number).toDouble(),
                    (it[1] as Number).toDouble(),
                    (it[2] as Number).toDouble())
                    .components.map { it / 100.0 }.toList()
        else
            null
    }
}