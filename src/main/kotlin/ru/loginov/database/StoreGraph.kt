package ru.loginov.database

import com.fasterxml.jackson.annotation.JsonIgnore
import javax.persistence.*

@Entity
class StoreMetricValues {
    @Id
    @GeneratedValue
    var id: Int? = null

    @ManyToMany(cascade = [CascadeType.ALL])
    val values: MutableMap<Double, ChemistryValue> = HashMap()

    @JsonIgnore
    fun getPoints(): Pair<ChemistryUnit?, Map<Double, Double>> {
        if (values.isEmpty()) {
            return null to emptyMap()
        }

        var minUnit: ChemistryUnit? = null

        values.map { it.value.unit }.forEach {
            if (it != null) {
                if (minUnit == null) {
                    minUnit = it
                } else if (minUnit!!.isParentFor(it)) {
                    minUnit = it
                }
            }
        }

        return minUnit to values.map { it.key to it.value.transformToUnit(minUnit!!) }.toMap()
    }
}

@Entity
class StoreGraph {
    @Id
    @GeneratedValue
    var id: Int? = null

    var name: String? = null

    @OneToMany(cascade = [CascadeType.ALL])
    val metricsValues: MutableMap<String, StoreMetricValues> = HashMap()
}