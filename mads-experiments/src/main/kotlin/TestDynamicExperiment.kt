package domain

import domain.mechanisms.DynSignals
import domain.mechanisms.simpleDynamicMechanism
import domain.objects.DynamicObject
import org.jetbrains.research.mads.core.configuration.Configuration
import org.jetbrains.research.mads.core.configuration.Pathway
import org.jetbrains.research.mads.core.configuration.configure
import org.jetbrains.research.mads.core.configuration.pathway
import org.jetbrains.research.mads.core.simulation.Model
import org.jetbrains.research.mads.core.types.EmptyConstants
import org.jetbrains.research.mads.core.types.SkipSaving

fun main() {
    createDynamicExperiment()
}

fun createDynamicExperiment() {
    val dynamic = DynamicObject(DynSignals())

    val pathwayDynamic: Pathway<DynamicObject> =
        pathway {
            mechanism(mechanism = DynamicObject::simpleDynamicMechanism,
                parameters = SimpleParameters(SkipSaving, EmptyConstants, 0.5)) {
                duration = 10
            }
        }

    val config = configure {
        addPathway(pathway = pathwayDynamic)
    }

    val s = Model(arrayListOf(dynamic), config)
    s.simulate { it.currentTime() > 100 }

    println((dynamic.signals as DynSignals).x)
}