package ru.loginov.database.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import ru.loginov.database.ChemistryUnit
import ru.loginov.database.ChemistryValue

@Repository
interface ChemistryValueRepository : JpaRepository<ChemistryValue, Int> {

    fun findAllByUnit(unit: ChemistryUnit): List<ChemistryValue>


}