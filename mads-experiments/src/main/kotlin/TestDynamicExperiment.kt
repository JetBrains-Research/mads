package domain

import domain.mechanisms.DynSignals
import domain.mechanisms.simpleAddMechanism
import domain.mechanisms.simpleDynamicMechanism
import domain.mechanisms.simpleMechanism
import domain.objects.DynamicObject
import domain.objects.HHCellObject
import org.jetbrains.research.mads.core.configuration.Configuration
import org.jetbrains.research.mads.core.configuration.Pathway
import org.jetbrains.research.mads.core.simulation.Model

fun main() {
    createDynamicExperiment()
}

fun createDynamicExperiment() {
    val dynamic = DynamicObject(DynSignals())

    val config = Configuration()

    val pathwayDynamic: Pathway<DynamicObject> = Pathway()
    pathwayDynamic.add(DynamicObject::simpleDynamicMechanism, SimpleParameters(0.5), 10) { true }

    config.add(DynamicObject::class, arrayListOf(pathwayDynamic))

    val s = Model(arrayListOf(dynamic), config)
    s.simulate { it.currentTime() > 100 }

    println((dynamic.signals as DynSignals).x)
}