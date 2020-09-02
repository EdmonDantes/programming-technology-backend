package ru.loginov.metric.factory.impl.mutator.rgb

import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component
import ru.loginov.metric.factory.impl.mutator.AbstractGetFactory

@Component
@Scope("singleton")
class BlueFactory : AbstractGetFactory() {
    override val keyTag: String = "RGB_BLUE"
    override val metricName: String = "Blue color"
    override val index: Int = 2
}