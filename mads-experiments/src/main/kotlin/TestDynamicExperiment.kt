package domain

import domain.mechanisms.DynSignals
import domain.mechanisms.simpleDynamicMechanism
import domain.objects.DynamicObject
import org.jetbrains.research.mads.core.configuration.Configuration
import org.jetbrains.research.mads.core.configuration.Pathway
import org.jetbrains.research.mads.core.simulation.Model
import org.jetbrains.research.mads.core.types.EmptyConstants
import org.jetbrains.research.mads.core.types.SkipSaving

fun main() {
    createDynamicExperiment()
}

fun createDynamicExperiment() {
    val dynamic = DynamicObject(DynSignals())

    val config = Configuration()

    val pathwayDynamic: Pathway<DynamicObject> = Pathway()
    pathwayDynamic.add(
        DynamicObject::simpleDynamicMechanism,
        SimpleParameters(SkipSaving, EmptyConstants, 0.5),
        10
    ) { true }

    config.add(DynamicObject::class, arrayListOf(pathwayDynamic))

    val s = Model(arrayListOf(dynamic), config)
    s.simulate { it.currentTime() > 100 }

    println((dynamic.signals as DynSignals).x)
}