package ru.loginov.database.manager

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import ru.loginov.database.ChemistryValue
import ru.loginov.database.repository.ChemistryValueRepository

@Service
class ChemistryValueManager(@Autowired private val unitManager: ChemistryUnitManager, @Autowired private val repository: ChemistryValueRepository) {

    fun save(value: ChemistryValue): ChemistryValue {
        if (value.unit == null) {
            return value
        }

        if (value.unit!!.id == null) {
            unitManager.save(value.unit!!)
        }

        return repository.save(value)
    }

}