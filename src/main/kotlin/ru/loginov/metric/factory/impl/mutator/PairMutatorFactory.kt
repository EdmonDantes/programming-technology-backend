package ru.loginov.metric.factory.impl.mutator

import ru.loginov.database.StoreGraph
import ru.loginov.metric.factory.MetricPartFactory
import ru.loginov.metric.impl.mutator.PairMutator

class PairMutatorFactory : MetricPartFactory<PairMutator> {
    override fun create(graph: StoreGraph, e: Double?, dependencyArgs: Array<*>): PairMutator {
        if (dependencyArgs.isEmpty()) {
            throw IllegalArgumentException("Have not enough arguments. Missing: \"first mutator factory\" and \"second mutator factory\"")
        }

        val firstMutator = dependencyArgs[0]?.let {
            if (it is MetricPartFactory<*>)
                it.create(graph, e, if (dependencyArgs.size >= 3 && dependencyArgs[3] is Array<*>) dependencyArgs[3] as Array<*> else emptyArray<Any>())
            else
                throw IllegalArgumentException("Argument \"first mutator factory\" not implement \"MetricPartFactory\"")
        }
        val secondMutator = dependencyArgs[1]?.let {
            if (it is MetricPartFactory<*>)
                it.create(graph, e, if (dependencyArgs.size >= 4 && dependencyArgs[4] is Array<*>) dependencyArgs[4] as Array<*> else emptyArray<Any>())
            else
                throw IllegalArgumentException("Argument \"second mutator factory\" not implement \"MetricPartFactory\"")
        }

        return PairMutator(firstMutator, secondMutator)
    }
}