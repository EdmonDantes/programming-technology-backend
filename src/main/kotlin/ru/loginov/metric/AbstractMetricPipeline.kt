package ru.loginov.metric

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.LinkedList
import java.util.concurrent.atomic.AtomicBoolean

abstract class AbstractMetricPipeline : MetricPipeline {

    protected val logger: Logger = LoggerFactory.getLogger(this.javaClass.name + "@" + this.hashCode())
    protected val parents: MutableList<MetricPipeline?> = ArrayList()
    protected val children: MutableList<MetricPipeline?> = ArrayList()
    protected val nameGettingLock = AtomicBoolean(false)

    override fun addChild(child: MetricPipeline) {
        children.add(child)
        if (child is AbstractMetricPipeline) {
            child.parents.add(this)
        }
    }

    override fun addParent(parent: MetricPipeline) {
        parents.add(parent)
        if (parent is AbstractMetricPipeline) {
            parent.children.add(this)
        }
    }

    override fun finish() {
        result().also { partResult ->
            children.forEach {
                it?.also { child ->
                    partResult?.also {
                        child.onNext(it, 0)
                    }
                    child.finish()
                }
            }
        }
    }

    override fun getResult(): List<*>? {
        val list = LinkedList<Any?>()
        result()?.also { list.addAll(it) }

        children.forEach {
            it?.getResult()?.also {
                list.addAll(it)
            }
        }

        return list
    }

    override fun getNamedResults(): Map<String, *>? {
        val map = HashMap<String, Any?>()

        getName()?.also { name ->
            result()?.also { result ->
                map[name] = result
            }
        }

        children.forEach {
            it?.getNamedResults()?.also {
                map.putAll(it)
            }
        }

        return map
    }

    override fun clearResult(): Boolean {
        val exception = Exception("Can not clear pipeline")

        try {
            if (!clear()) {
                exception.addSuppressed(RuntimeException("Can not clear this pipeline"))
            }
        } catch (e: Exception) {
            exception.addSuppressed(e)
        }

        children.forEach {
            try {
                it?.clearResult()
            } catch (e: Exception) {
                exception.addSuppressed(RuntimeException("Can not clear child pipeline", e))
            }
        }

        if (exception.suppressed.isEmpty()) {
            return true
        }

        logger.error("Error on clear result", exception)
        return false
    }

    override fun getName(): String? {
        return if (nameGettingLock.getAndSet(true)) null else {
            try {
                name()
            } finally {
                nameGettingLock.set(false)
            }
        }
    }

    protected open fun clear(): Boolean = true

    protected open fun result(): List<*>? = null

    protected open fun name() : String? = null
}