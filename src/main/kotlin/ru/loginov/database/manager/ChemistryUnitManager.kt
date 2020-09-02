package ru.loginov.database.manager

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Service
import ru.loginov.database.ChemistryUnit
import ru.loginov.database.repository.ChemistryUnitRepository

@Service
@Scope("singleton")
class ChemistryUnitManager(@Autowired private val repository: ChemistryUnitRepository) {

    fun save(unit: ChemistryUnit) : ChemistryUnit {
        return repository.save(unit)
    }

}