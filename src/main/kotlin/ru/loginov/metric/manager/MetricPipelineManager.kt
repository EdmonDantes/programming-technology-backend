package ru.loginov.metric.manager

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component
import ru.loginov.metric.description.MetricDescription
import ru.loginov.metric.description.ShortMetricDescription
import ru.loginov.metric.factory.MetricPartFactory
import ru.loginov.metric.manager.pipeline.builder.impl.DefaultMetricPipelineBuilder

@Component
@Scope("singleton")
class MetricPipelineManager(
        @Autowired private val metricsDescriptions: List<MetricDescription>,
        @Autowired metricsFactories: List<MetricPartFactory<*>>,
        @Autowired @Qualifier("saver") saversFactories: List<MetricPartFactory<*>>
) {

    private val factoryClassToFactory = metricsFactories.map { it.javaClass to it }.toMap()
    private val descriptionTagToDescription = metricsDescriptions.map { it.tag to it }.toMap()
    private val factoryClassToDescription = metricsDescriptions.map { it.factoryClass to it }.toMap().toMutableMap()
    private val saversDescriptions = ArrayList<MetricDescription>()

    init {
        val saversFactoriesClasses = saversFactories.map { it.javaClass }
        var index = 1
        metricsFactories.forEach {
            if (factoryClassToDescription[it.javaClass] == null) {
                factoryClassToDescription[it.javaClass] = object : MetricDescription {
                    override val tag: String = "generated_tag_id_" + index++
                    override val factoryClass: Class<out MetricPartFactory<*>> = it.javaClass
                }

                if (saversFactoriesClasses.contains(it.javaClass)) {
                    factoryClassToDescription[it.javaClass]?.let { saversDescriptions.add(it) }
                }
            }
        }
    }

    fun getAllMetrics(): List<MetricDescription> = metricsDescriptions

    fun createReadPipeline(shortDescriptions: List<ShortMetricDescription>) : DefaultMetricPipelineBuilder {
        val builder = DefaultMetricPipelineBuilder(factoryClassToDescription, factoryClassToFactory)

        val ids = HashMap<String, Int>()

        val descriptions = shortDescriptions.map { descriptionTagToDescription[it.type] to it }.filter { it.first != null }.sortedBy { it.second.name }

        descriptions.filter { !it.first!!.isCollector }.forEach {pair ->
            builder.addMutator(pair.first!!)?.also { if (it > -1) ids[pair.second.name] = it }
        }

        descriptions.filter { it.first!!.isCollector && it.second.collectNames != null && it.second.collectNames!!.isNotEmpty() }.forEach { pair ->
            builder.addCollector(pair.first!!, pair.second.collectNames!!.mapNotNull { ids[it] })?.also { if (it > -1) ids[pair.second.name] = it }
        }

        return builder
    }

    fun createSavePipeline() : DefaultMetricPipelineBuilder {
        val builder = DefaultMetricPipelineBuilder(factoryClassToDescription, factoryClassToFactory)

        saversDescriptions.forEach {
            builder.addSaver(it)
        }

        return builder
    }

}