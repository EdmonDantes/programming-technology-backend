package ru.loginov.database.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.loginov.database.ChemistryUnit

interface ChemistryUnitRepository : JpaRepository<ChemistryUnit, Int>