package ru.loginov.database.manager

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import ru.loginov.database.StoreGraph
import ru.loginov.database.repository.StoreGraphRepository

@Service
class StoreGraphManager(@Autowired private val graphRepository: StoreGraphRepository, @Autowired private val valuesManager: StoreMetricValuesManager) {

    fun save(graph: StoreGraph) : StoreGraph {
        graph.metricsValues.values.forEach {
            valuesManager.save(it)
        }

        return graphRepository.save(graph);
    }

    fun getById(id: Int): StoreGraph? = graphRepository.findById(id).orElse(null)

    fun delete(id: Int): Boolean {
        return try {
            graphRepository.deleteById(id)
            true
        } catch (e: Exception) {
            false
        }
    }

    fun getAll(): List<StoreGraph> = graphRepository.findAll()

    fun getAllByStoreMetrics(id: Int) : List<Int> {
        return graphRepository.findAllByStoreMetric(id);
    }
}