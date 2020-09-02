package ru.loginov.database

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.OneToOne

@Entity
class ChemistryUnit {

    @Id
    @GeneratedValue
    var id: Int? = null

    @Column(nullable = false, unique = true)
    var name: String = ""
        set(value) {
            field = value.toLowerCase()
        }

    @OneToOne
    @JsonSerialize(using = ChemistryUnitSerializer::class)
    var moreChemistryUnit: ChemistryUnit? = null

    @JsonIgnore
    var scaleToMore: Double? = null

    @OneToOne
    @JsonSerialize(using = ChemistryUnitSerializer::class)
    var lessChemistryUnit: ChemistryUnit? = null

    @JsonIgnore
    var scaleToLess: Double? = null

    fun isChained(unit: ChemistryUnit) = id == unit.id || chainedInMoreUnit(unit) || chainedInLessUnit(unit)

    fun isParentFor(unit: ChemistryUnit) = chainedInLessUnit(unit)

    fun isChildFor(unit: ChemistryUnit) = chainedInMoreUnit(unit)

    @JsonIgnore
    fun getScaleTo(unit: ChemistryUnit) : Double {

        when {
            unit.id == id -> {
                return 1.0
            }
            chainedInMoreUnit(unit) -> {
                return moreChemistryUnit?.let { moreUnit -> scaleToMore?.let { scale -> moreUnit.getScaleTo(unit) * scale } }
                        ?: throw IllegalStateException("Mutated unit")
            }
            chainedInLessUnit(unit) -> {
                return lessChemistryUnit?.let { lessUnit -> scaleToLess?.let { scale -> lessUnit.getScaleTo(unit) * scale } }
                        ?: throw IllegalStateException("Mutated unit")
            }
            else -> {
                throw IllegalArgumentException("Not chained unit")
            }
        }

    }

    private fun chainedInMoreUnit(unit: ChemistryUnit) : Boolean {
        return moreChemistryUnit?.let { it.id == unit.id  || it.chainedInMoreUnit(unit) } ?: false
    }


    private fun chainedInLessUnit(unit: ChemistryUnit) : Boolean {
        return lessChemistryUnit?.let { it.id == unit.id || it.chainedInLessUnit(unit) } ?: false
    }

}


class ChemistryUnitSerializer : JsonSerializer<ChemistryUnit>() {
    override fun serialize(value: ChemistryUnit?, gen: JsonGenerator?, serializers: SerializerProvider?) {
        gen?.also {
            value?.id?.also { id ->
                it.writeNumber(id)
            } ?: it.writeNull()
        }
    }

}