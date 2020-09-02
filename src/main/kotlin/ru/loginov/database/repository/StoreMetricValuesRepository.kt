package ru.loginov.database.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import ru.loginov.database.StoreMetricValues

interface StoreMetricValuesRepository : JpaRepository<StoreMetricValues, Int> {

    @Query(value="SELECT store_metric_values_id as id FROM store_metric_values_values where values_id = ?1", nativeQuery = true)
    fun findAllByUnit(id: Int): List<StoreMetricValues>

}