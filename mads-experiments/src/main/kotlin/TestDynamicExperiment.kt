package domain

import domain.mechanisms.*
import domain.objects.DynamicObject
import domain.objects.HHCellObject
import domain.objects.HHSignals
import org.jetbrains.research.mads.core.configuration.Configuration
import org.jetbrains.research.mads.core.configuration.Pathway
import org.jetbrains.research.mads.core.simulation.Model

fun main() {
    createDynamicExperiment()
}

fun createDynamicExperiment() {
    val dynamic = HHCellObject(HHSignals())

    val config = Configuration()

    val pathwayDynamic: Pathway<HHCellObject> = Pathway()
    pathwayDynamic.add(HHCellObject::IDynamicMechanism, SimpleParameters(1.0), 10) { it.forCondition }
    pathwayDynamic.add(HHCellObject::VDynamicMechanism, SimpleParameters(1.0), 10) { it.forCondition }
    pathwayDynamic.add(HHCellObject::NDynamicMechanism, SimpleParameters(1.0), 10) { it.forCondition }
    pathwayDynamic.add(HHCellObject::MDynamicMechanism, SimpleParameters(1.0), 10) { it.forCondition }
    pathwayDynamic.add(HHCellObject::HDynamicMechanism, SimpleParameters(1.0), 10) { it.forCondition }


    config.add(HHCellObject::class, arrayListOf(pathwayDynamic))

    val s = Model(arrayListOf(dynamic), config)
    s.simulate { it.currentTime() > 100 }

    println((dynamic.signals as HHSignals))
}