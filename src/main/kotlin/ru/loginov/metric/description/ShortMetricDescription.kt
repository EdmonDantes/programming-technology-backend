package ru.loginov.metric.description

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class ShortMetricDescription @JsonCreator constructor(@JsonProperty("name") val name: String, @JsonProperty("type") val type: String, @JsonProperty("collectNames") val collectNames: List<String>? = null)