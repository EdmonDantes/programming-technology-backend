package ru.loginov.metric.description.impl.saver.rgb

import org.springframework.beans.factory.annotation.Qualifier
import ru.loginov.metric.description.MetricDescription
import ru.loginov.metric.factory.MetricPartFactory
import ru.loginov.metric.factory.impl.saver.rgb.RedSaverFactory

@Qualifier("saver")
class RedSaverDescription : MetricDescription{
    override val tag: String = "RGB_RED_SAVER"
    override val factoryClass: Class<out MetricPartFactory<*>> = RedSaverFactory::class.java
}