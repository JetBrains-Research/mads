package domain

import domain.objects.HHCellObject
import org.jetbrains.research.mads.core.configuration.Configuration
import org.jetbrains.research.mads.core.configuration.Pathway
import org.jetbrains.research.mads.core.simulation.Model

fun main() {
    createHHCellsExperiment()
}

fun createHHCellsExperiment()
{
//    val hhCell = HHCellObject(HHSignals(-65.0, .0, .0, .0))
//
//    val config = Configuration()
//
//    val pathwayHH: Pathway<HHCellObject> = Pathway()
//    pathwayHH.add(HHCellObject::IDynamicMechanism, SimpleParameters(1.0), 10) { it.forCondition }
//    pathwayHH.add(HHCellObject::VDynamicMechanism, SimpleParameters(1.0), 10) { it.forCondition }
//    pathwayHH.add(HHCellObject::HDynamicMechanism, SimpleParameters(1.0), 10) { it.forCondition }
//    pathwayHH.add(HHCellObject::MDynamicMechanism, SimpleParameters(1.0), 10) { it.forCondition }
//    pathwayHH.add(HHCellObject::NDynamicMechanism, SimpleParameters(1.0), 10) { it.forCondition }
//
//    config.add(HHCellObject::class, arrayListOf(pathwayHH))
//
//    val s = Model(arrayListOf(hhCell), config)
//    s.simulate { it.currentTime() > 100 }
}