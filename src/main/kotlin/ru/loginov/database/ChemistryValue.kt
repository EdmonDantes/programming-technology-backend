package ru.loginov.database

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.ManyToOne

@Entity
class ChemistryValue {

    @Id
    @GeneratedValue
    var id: Int? = null

    var value: Double? = null

    @ManyToOne
    var unit: ChemistryUnit? = null

    fun transformToUnit(unit: ChemistryUnit) : Double {
        return this.unit?.getScaleTo(unit)?.let { scale -> this.value?.let { it * scale }} ?: throw IllegalStateException("Units can not be null")
    }

}