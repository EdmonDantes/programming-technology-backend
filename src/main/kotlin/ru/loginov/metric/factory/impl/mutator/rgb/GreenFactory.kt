package ru.loginov.metric.factory.impl.mutator.rgb

import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component
import ru.loginov.metric.factory.impl.mutator.AbstractGetFactory

@Component
@Scope("singleton")
class GreenFactory : AbstractGetFactory() {
    override val keyTag: String = "RGB_GREEN"
    override val metricName: String = "Green color"
    override val index: Int = 1
}