package domain

import domain.objects.HHCellObject
import org.jetbrains.research.mads.core.configuration.Configuration
import org.jetbrains.research.mads.core.configuration.Pathway
import org.jetbrains.research.mads.core.simulation.Model

fun main() {
    createCellsExperiment()
    createHHCellsExperiment()
}

fun createCellsExperiment()
{
    val simple = SimpleObject()
    val dummy = DummyObject()

    val config = Configuration()

    val pathwaySimple: Pathway<SimpleObject> = Pathway()
    val pathwayDummy: Pathway<DummyObject> = Pathway()
    pathwaySimple.add(SimpleObject::simpleMechanism, SimpleParameters(0.5), 10) { it.forCondition }
    pathwaySimple.add(SimpleObject::simpleAddMechanism, SimpleParameters(0.5), 10) { it.forCondition }
    pathwayDummy.add(DummyObject::simpleMechanism, SimpleParameters(0.8), 10) { it.forCondition }

    config.add(SimpleObject::class, arrayListOf(pathwaySimple))
    config.add(DummyObject::class, arrayListOf(pathwayDummy))

    val s = Model(arrayListOf(simple, dummy), config)
    s.simulate { it.currentTime() > 100 }

    println(s.recursivelyGetChildObjects().size)
}

fun createHHCellsExperiment()
{
    val hhCell = HHCellObject()

    val config = Configuration()

    val pathwayHH: Pathway<HHCellObject> = Pathway()
    pathwayHH.add(HHCellObject::IDynamicMechanism, SimpleParameters(1.0), 10) { it.forCondition }
    pathwayHH.add(HHCellObject::VDynamicMechanism, SimpleParameters(1.0), 10) { it.forCondition }
    pathwayHH.add(HHCellObject::HDynamicMechanism, SimpleParameters(1.0), 10) { it.forCondition }
    pathwayHH.add(HHCellObject::MDynamicMechanism, SimpleParameters(1.0), 10) { it.forCondition }
    pathwayHH.add(HHCellObject::NDynamicMechanism, SimpleParameters(1.0), 10) { it.forCondition }

    config.add(HHCellObject::class, arrayListOf(pathwayHH))

    val s = Model(arrayListOf(hhCell), config)
    s.simulate { it.currentTime() > 100 }
}