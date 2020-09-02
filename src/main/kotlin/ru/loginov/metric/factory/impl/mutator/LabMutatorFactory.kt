package ru.loginov.metric.factory.impl.mutator

import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component
import ru.loginov.color.impl.lab.LabColorSpace
import ru.loginov.database.StoreGraph
import ru.loginov.metric.factory.MetricPartFactory
import ru.loginov.metric.impl.mutator.LabMutator

@Component
@Scope("singleton")
class LabMutatorFactory(val colorSpace: LabColorSpace) : MetricPartFactory<LabMutator> {

    override fun create(graph: StoreGraph, e: Double?, dependencyArgs: Array<*>): LabMutator {
        return LabMutator(colorSpace);
    }

}