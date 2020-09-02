package ru.loginov.database.manager

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import ru.loginov.database.StoreMetricValues
import ru.loginov.database.repository.StoreMetricValuesRepository

@Service
class StoreMetricValuesManager(@Autowired private val valueManager: ChemistryValueManager, @Autowired private val repository: StoreMetricValuesRepository) {

    fun save(value: StoreMetricValues) : StoreMetricValues {
        value.values.values.forEach {
            valueManager.save(it)
        }

        return repository.save(value)
    }

}