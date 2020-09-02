package ru.loginov.metric.factory.impl.mutator

import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component
import ru.loginov.database.StoreGraph
import ru.loginov.metric.factory.MetricPartFactory
import ru.loginov.metric.impl.mutator.FilterMutator

@Component
@Scope("singleton")
class FilterMutatorFactory : MetricPartFactory<FilterMutator> {
    override fun create(graph: StoreGraph, e: Double?, dependencyArgs: Array<*>): FilterMutator {
        if (dependencyArgs.isNotEmpty()) {
            if (dependencyArgs[0] is Number) {
                return FilterMutator((dependencyArgs[0] as Number).toInt())
            } else {
                throw IllegalArgumentException("First dependency argument is not number")
            }
        } else {
            throw IllegalArgumentException("Dependency arguments must be more than 1")
        }
    }
}