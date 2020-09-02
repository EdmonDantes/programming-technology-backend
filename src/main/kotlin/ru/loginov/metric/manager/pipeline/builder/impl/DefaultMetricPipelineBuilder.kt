package ru.loginov.metric.manager.pipeline.builder.impl

import ru.loginov.database.StoreGraph
import ru.loginov.metric.MetricPipeline
import ru.loginov.metric.description.MetricDescription
import ru.loginov.metric.factory.MetricPartFactory
import ru.loginov.metric.impl.saver.MetricSaver
import ru.loginov.metric.manager.pipeline.RootMetricPipeline
import ru.loginov.metric.manager.pipeline.builder.MetricPipelineBuilder
import java.util.TreeMap
import java.util.concurrent.atomic.AtomicInteger
import java.util.function.Consumer

open class DefaultMetricPipelineBuilder (
        protected val factoryClassToDescription: Map<Class<out MetricPartFactory<*>>, MetricDescription>,
        protected val factoryClassToFactory: Map<Class<MetricPartFactory<*>>, MetricPartFactory<*>>,
        protected val description: MetricDescription? = null,
        protected val id: Int? = null
) : MetricPipelineBuilder {

    companion object {
        val CHILD_COMPARATOR = Comparator<Pair<Class<out MetricPartFactory<*>>?, Array<*>>> { a, b ->
            a.first.hashCode().compareTo(b.first.hashCode()).let {
                if (it == 0)
                    a.second.contentDeepHashCode().compareTo(b.second.contentDeepHashCode())
                else
                    it
            }
        }

        val nextId = AtomicInteger(1)
    }

    private var onBuildAction: Consumer<MetricPipeline>? = null

    private val factory = getFactory(description)

    private var builtPipeline: MetricPipeline? = null

    private val _children = TreeMap<Pair<Class<out MetricPartFactory<*>>?, Array<*>>, DefaultMetricPipelineBuilder>(CHILD_COMPARATOR)

    protected val children: Map<Pair<Class<out MetricPartFactory<*>>?, Array<*>>, DefaultMetricPipelineBuilder>
        get() = _children

    override fun build(graph: StoreGraph, e: Double?, dependencyArgs: Array<*>): MetricPipeline {
        if (builtPipeline == null) {
            builtPipeline = onBuild(graph, e, dependencyArgs)
            builtPipeline?.also {
                onBuildAction?.accept(it)
            }
        }

        return builtPipeline!!
    }

    private fun onBuild(graph: StoreGraph, e: Double?, dependencyArgs: Array<*> = emptyArray<Any>()): MetricPipeline? = (factory?.create(graph, e, dependencyArgs) ?: RootMetricPipeline()).apply { children.forEach { addChild(it.value.build(graph, e, it.key.second)) } }

    protected fun addChild(factory: MetricPartFactory<*>, dependencyArgs: Array<Any> = emptyArray()): DefaultMetricPipelineBuilder {
        return _children.computeIfAbsent(factory.javaClass to dependencyArgs) {
            factoryClassToDescription[it.first]?.let { createNewBuilder(it, nextId.getAndIncrement()) } ?: throw IllegalStateException("Can not find description for factory $factory")
        }
    }

    protected fun addChild(builder: DefaultMetricPipelineBuilder, dependencyArgs: Array<*> = emptyArray<Any>()) {
        val factoryClass = builder.description?.factoryClass
        _children[factoryClass to dependencyArgs] = builder
    }

    override fun addMutator(description: MetricDescription, dependencies: Iterator<Class<out MetricPartFactory<*>>>?): Int? {
        val mutatorFactory = getFactory(description) ?: return null

        val tmp = createDependencies(description, mutatorFactory)

        return tmp.second
    }

    override fun addCollector(description: MetricDescription, collectIds: Collection<Int>?) : Int? {
        val collectorFactory = getFactory(description) ?: return null

        val root = DefaultMetricPipelineBuilder(factoryClassToDescription, factoryClassToFactory)
        val tmp = createDependencies(description, collectorFactory, true, root)
        tmp.second?.let { root.getById(it) }?.also { builder ->
            collectIds?.mapNotNull { getById(it) }?.forEach {
                it.addChild(builder)
            }

            return builder.id
        }

        return null
    }

    override fun addSaver(description: MetricDescription): Int? {
        val saverFactory = getFactory(description) ?: return null

        val builder = DefaultMetricPipelineBuilder(factoryClassToDescription, factoryClassToFactory)
        createDependencies(description, saverFactory, false, builder)

        val saverBuilder = createNewBuilder(description, nextId.getAndIncrement())

        saverBuilder.onBuildAction = Consumer { if (it is MetricSaver) it.builder = builder }

        addChild(saverBuilder)

        return saverBuilder.id

    }

    protected fun getFactory(description: MetricDescription?) : MetricPartFactory<*>? = description?.let { factoryClassToFactory[it.factoryClass] }

    protected fun getFactory(factoryClass: Class<out MetricPartFactory<*>>?) : MetricPartFactory<*>? = factoryClass?.let { factoryClassToFactory[it] }

    protected fun getDescription(factoryClass: Class<out MetricPartFactory<*>>?) : MetricDescription? = factoryClass?.let { factoryClassToDescription[it] }

    protected fun createNewBuilder(description: MetricDescription, id: Int) : DefaultMetricPipelineBuilder = DefaultMetricPipelineBuilder(factoryClassToDescription, factoryClassToFactory, description, id)

    private fun createDependencies(description: MetricDescription, factory: MetricPartFactory<*>, isAddSelf: Boolean = true, parent: DefaultMetricPipelineBuilder = this) : Pair<Int?, Int?> {
        val iterator = factory.dependencies.iterator()

        var last: DefaultMetricPipelineBuilder = parent
        var lastBuilder: DefaultMetricPipelineBuilder? = null

        if (isAddSelf) {
            lastBuilder = createNewBuilder(description, nextId.getAndIncrement())
        }

        if (!iterator.hasNext()) {
            return null to lastBuilder?.also{ last.addChild(it) }?.id
        }



        var result: DefaultMetricPipelineBuilder? = null

        while (iterator.hasNext()) {
            val dependencyClass = iterator.next()
            factoryClassToFactory[dependencyClass]?.let { dependencyFactory ->
                val dependencyArgs = factory.getDependenciesArguments(dependencyFactory)

                factoryClassToDescription[dependencyClass]?.also { description ->
                    last = last.let { it._children.computeIfAbsent(dependencyClass to dependencyArgs) { createNewBuilder(description, nextId.getAndIncrement()) } }
                    if (result == null) {
                        result = last
                    }
                }
            }
        }


        lastBuilder?.let { last.addChild(it) }

        return result?.id to lastBuilder?.id
    }

    private fun getById(id: Int) : DefaultMetricPipelineBuilder? {
        if (this.id == id) {
            return this
        }

        children.forEach {
            it.value.getById(id)?.also {
                return it
            }
        }

        return null
    }

}