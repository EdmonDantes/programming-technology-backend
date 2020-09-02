package ru.loginov.database.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import ru.loginov.database.StoreGraph

@Repository
interface StoreGraphRepository : JpaRepository<StoreGraph, Int> {

    @Query(nativeQuery = true, value = "select store_graph_id as id from store_graph_metrics_values where metrics_values_id = ?1")
    fun findAllByStoreMetric(id: Int) : List<Int>
}