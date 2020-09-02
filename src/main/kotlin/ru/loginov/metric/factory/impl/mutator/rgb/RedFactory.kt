package ru.loginov.metric.factory.impl.mutator.rgb

import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component
import ru.loginov.metric.factory.impl.mutator.AbstractGetFactory

@Component
@Scope("singleton")
class RedFactory : AbstractGetFactory() {
    override val keyTag: String = "RGB_RED"
    override val metricName: String = "Red color"
    override val index: Int = 0
}