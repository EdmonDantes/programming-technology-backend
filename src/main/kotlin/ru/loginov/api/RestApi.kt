package ru.loginov.api

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.commons.net.util.Base64
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.*
import ru.loginov.color.impl.lab.LabColorSpace
import ru.loginov.database.ChemistryUnit
import ru.loginov.database.ChemistryValue
import ru.loginov.database.StoreGraph
import ru.loginov.database.manager.StoreGraphManager
import ru.loginov.database.repository.ChemistryUnitRepository
import ru.loginov.database.repository.ChemistryValueRepository
import ru.loginov.database.repository.StoreMetricValuesRepository
import ru.loginov.metric.description.ShortMetricDescription
import ru.loginov.metric.manager.MetricPipelineManager
import java.io.ByteArrayInputStream
import java.util.*
import java.util.zip.Inflater
import java.util.zip.InflaterInputStream
import kotlin.streams.toList

@RestController
class RestApi(
        @Autowired val graphs: StoreGraphManager,
        @Autowired val units: ChemistryUnitRepository,
        @Autowired val valuesRepository: ChemistryValueRepository,
        @Autowired val storeMetricValuesRepository: StoreMetricValuesRepository,
        @Autowired val manager: MetricPipelineManager) {

    @Value("\${chemistry.api.version}")
    private var apiVersion: String? = null

    private val logger = LoggerFactory.getLogger(this.javaClass.name + "@" + this.hashCode())

    companion object {
        val mapper = ObjectMapper()
        val colorSpace = LabColorSpace()
    }

    @RequestMapping(path = ["/"], method = [RequestMethod.POST, RequestMethod.GET])
    fun root(): JsonNode = mapper.createObjectNode().put("message", "Chemistry Api").put("version", apiVersion ?: "None")

    @RequestMapping(path = ["/unit/create"], method = [RequestMethod.POST])
    @ResponseBody
    fun createUnit(@RequestBody body: JsonNode) : JsonNode {
        logger.debug("createUnit: start")
        val unit = ChemistryUnit()
        unit.name = body["name"]?.asText() ?: return createJsonError("Not find name")
        body["less"]?.also {
            if (!it.isNull) {
                if (it["id"] == null || it["scale"] == null) {
                    return createJsonError("Can not find \"id\" and/or \"scale\" fields in \"less\" unit")
                }

                unit.lessChemistryUnit = units.findById(it["id"].asInt()).orElse(null)
                unit.scaleToLess = it["scale"].asDouble()
            }
        }

        body["more"]?.also {
            if (!it.isNull) {
                if (it["id"] == null || it["scale"] == null) {
                    return createJsonError("Can not find \"id\" and/or \"scale\" fields in \"more\" unit")
                }

                unit.moreChemistryUnit = units.findById(it["id"].asInt()).orElse(null)
                unit.scaleToMore = it["scale"].asDouble()
            }
        }

        if (unit.lessChemistryUnit != null && unit.lessChemistryUnit!!.moreChemistryUnit != null) {
            return createJsonError("Can not set \"less\" unit")
        }

        if (unit.moreChemistryUnit != null && unit.moreChemistryUnit!!.lessChemistryUnit != null) {
            return createJsonError("Can not set \"more\" unit")
        }

        val result = units.save(unit)

        if (unit.lessChemistryUnit != null) {
            unit.lessChemistryUnit!!.moreChemistryUnit = result
            unit.lessChemistryUnit!!.scaleToMore = 1 / unit.scaleToLess!!
            unit.lessChemistryUnit = units.save(unit.lessChemistryUnit!!)
        }

        if (unit.moreChemistryUnit != null) {
            unit.moreChemistryUnit!!.lessChemistryUnit = result
            unit.moreChemistryUnit!!.scaleToLess = 1 / unit.scaleToMore!!
            unit.moreChemistryUnit = units.save(unit.moreChemistryUnit!!)
        }

        logger.debug("createUnit: end")

        return createAnswer(result)
    }

    @RequestMapping(path = ["/unit/all"], method = [RequestMethod.POST, RequestMethod.GET])
    @ResponseBody
    fun getUnits() : JsonNode = mapper.createObjectNode().apply {
        put("status", true)
        putArray("result").apply {
            units.findAll().forEach {
                this.addPOJO(it)
            }
        }
    }

    @RequestMapping(path = ["/unit/remove"], method = [RequestMethod.POST, RequestMethod.GET])
    @ResponseBody
    fun removeUnits(@RequestBody body: JsonNode) : JsonNode {
        val id = body["id"]?.asInt() ?: return createJsonError("Can not find field \"id\"")

        val unit = units.findById(id).orElse(null) ?:  return createJsonError("Can not find unit with id = \"id\"")

        unit.lessChemistryUnit?.also {
            unit.moreChemistryUnit = null
            units.save(unit)
        }

        unit.moreChemistryUnit?.also {
            unit.lessChemistryUnit = null
            units.save(unit)
        }

        val forceDelete = body["force"]?.asBoolean() ?: false

        val storeMetricValues = valuesRepository.findAllByUnit(unit).stream()
            .map{ it.id }
            .filter { it != null }
            .map { storeMetricValuesRepository.findAllByUnit(it!!) }
            .flatMap { it.stream() }
            .map { it.id }
            .filter{ it != null }
            .map { graphs.getAllByStoreMetrics(it!!) }
            .flatMap { it.stream() }
            .toList()

        if (storeMetricValues.isNotEmpty()) {
            logger.info("Need force delete for unit with id = ${unit.id}")
            if (!forceDelete) {
                return createAnswer(mapper.createObjectNode().put("requires", "force").put("reason", "Have graphs which use this unit"))
            } else {
                storeMetricValues.forEach {
                    it?.also {
                        try {
                            graphs.delete(it)
                        } catch (e: Exception) {
                            logger.warn("Can not delete graph with id = $it", e)
                        }
                    }
                }
            }
        }

        units.delete(unit)

        return createAnswer()
    }

    @RequestMapping(path = ["/save"], method = [RequestMethod.POST])
    @ResponseBody
    fun saveMetrics(@RequestBody root: JsonNode) : JsonNode? {
        logger.debug("saveMetrics: create")
        val graph = StoreGraph()

        graph.name = root["name"].asText(null) ?: return createJsonError("Can not get \"name\" field")

        val pipeline = manager.createSavePipeline().build(graph, null)

        root["colors"]?.elements()?.forEach { node ->
            node["value"]?.let { value ->
                val ret = ChemistryValue()
                ret.unit = units.findById(value["unit"]?.asInt() ?: return createJsonError("Can not find unit id in value")).orElse(null) ?: return mapper.createObjectNode().put("error", "Can not find unit with id ${value["unit"].asInt()}")
                ret.value = value["value"]?.asDouble() ?: return createJsonError("Can not find number value in field \"value\"")
                ret
            }?.also { chemistryValue ->
                node["color"]?.let {
                    val list = LinkedList<Any>()
                    list.add(chemistryValue)
                    parseColors(it) {rgb -> list.add(rgb)}
                    list
                }?.also {
                    pipeline.onNext(it)
                }
            } ?: return createJsonError("Can not find value in colors element")
        } ?: return createJsonError("Can not find \"colors\" field")

        pipeline.finish()

        logger.debug("saveMetrics: end")

        return createAnswer(graphs.save(graph))
    }

    @RequestMapping(path = ["/metrics/all"], method = [RequestMethod.POST, RequestMethod.GET])
    @ResponseBody
    fun getMetrics() = createAnswer(manager.getAllMetrics())

    @RequestMapping(path = ["/get"], method = [RequestMethod.POST])
    @ResponseBody
    fun getResult(@RequestBody node: JsonNode) : JsonNode? {
        logger.debug("getResult: start")
        node["graph"]?.asInt()?.also {
            val e = node["e"]?.asDouble()
            graphs.getById(it)?.also {graph ->
                node["metrics"]?.also {
                    val pipeline = manager.createReadPipeline(mapper.readerFor(object : TypeReference<List<ShortMetricDescription>>(){}).readValue(mapper.treeAsTokens(it)))
                            .build(graph, e)

                    node["colors"]?.also {colors ->
                        parseColors(colors) { list -> pipeline.onNext(list) }
                        pipeline.finish()
                        logger.debug("getResult: end")
                        return createAnswer(pipeline.getNamedResults() as Any)
                    } ?: return createJsonError("Can not find field \"colors\"")
                } ?: return createJsonError("Can not find field \"metrics\"")
            } ?: return createJsonError("Can not find graph with id = $it")
        } ?: return createJsonError("Can not find field \"graph\"")

        return null
    }

    @RequestMapping(path = ["/delete"], method = [RequestMethod.POST])
    @ResponseBody
    fun deleteGraph(@RequestBody node: JsonNode) : JsonNode {
        return node["id"]?.asInt()?.let {
            if (graphs.delete(it))
                createAnswer(true)
            else
                createJsonError("Can not delete graph with id $it")
        } ?: createJsonError("Can not find field \"id\"")
    }

    @RequestMapping(path = ["/all"], method = [RequestMethod.POST, RequestMethod.GET])
    @ResponseBody
    fun getAllGraphs() : JsonNode = createAnswer(graphs.getAll())

    private fun createJsonError(msg: String) : JsonNode {
        return mapper.createObjectNode().put("status", false).put("error", msg)
    }

    private fun createAnswer() : JsonNode {
        return mapper.createObjectNode().put("status", true)
    }
    private fun createAnswer(obj: Any) : JsonNode {
        return mapper.createObjectNode().put("status", true).putPOJO("result", obj)
    }

    private fun parseColors(colors: JsonNode, onNextRGB: (List<Double>) -> Unit) {
        if (colors.isArray) {
            colors
                    .elements()
                    .asSequence()
                    .mapNotNull { it["r"]?.asDouble()?.let{r -> it["g"]?.asDouble()?.let { g -> it["b"]?.asDouble()?.let { b -> arrayListOf(r / 255.0, g / 255.0, b / 255.0) } } } }
                    .forEach {
                        onNextRGB.invoke(it)
                    }
        } else {
            if (colors.has("base64")) {
                colors["base64"].asText().also {
                    val byteArray = Base64.decodeBase64(it)

                    ByteArrayInputStream(byteArray).use {
                        InflaterInputStream(it, Inflater(true)).use {
                            while (it.available() > 0) {
                                val r = it.read()
                                val g = if (it.available() > 0) it.read() else null
                                val b = if (it.available() > 0) it.read() else null

                                if (g != null && b != null) {
                                    onNextRGB.invoke(arrayListOf(r.toDouble() / 255.0, g.toDouble() / 255.0, b.toDouble() / 255.0))
                                }
                            }
                        }
                    }
                }
            } else {
                val r = colors["r"]?.asDouble()
                val g = colors["g"]?.asDouble()
                val b = colors["b"]?.asDouble()

                if (r != null && g != null && b != null) {
                    onNextRGB.invoke(arrayListOf(r / 255.0, g / 255.0, b / 255.0))
                }
            }
        }
    }
}
