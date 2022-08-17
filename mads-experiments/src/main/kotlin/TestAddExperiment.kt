package domain

import domain.mechanisms.simpleAddMechanism
import domain.mechanisms.simpleMechanism
import org.jetbrains.research.mads.core.configuration.Configuration
import org.jetbrains.research.mads.core.configuration.Pathway
import org.jetbrains.research.mads.core.simulation.Model
import org.jetbrains.research.mads.core.types.EmptyConstants
import org.jetbrains.research.mads.core.types.SkipSaving

fun main() {
    createCellsExperiment()
}

fun createCellsExperiment() {
    val simple = SimpleObject()
    val dummy = DummyObject()

    val config = Configuration()

    val pathwaySimple: Pathway<SimpleObject> = Pathway()
    val pathwayDummy: Pathway<DummyObject> = Pathway()
    pathwaySimple.add(
        SimpleObject::simpleMechanism,
        SimpleParameters(SkipSaving, EmptyConstants, 0.5),
        10
    ) { it.forCondition }
    pathwaySimple.add(
        SimpleObject::simpleAddMechanism,
        SimpleParameters(SkipSaving, EmptyConstants, 0.5),
        10
    ) { it.forCondition }
    pathwayDummy.add(
        DummyObject::simpleMechanism,
        SimpleParameters(SkipSaving, EmptyConstants, 0.8),
        10
    ) { it.forCondition }

    config.add(SimpleObject::class, arrayListOf(pathwaySimple))
    config.add(DummyObject::class, arrayListOf(pathwayDummy))

    val s = Model(arrayListOf(simple, dummy), config)
    s.simulate { it.currentTime() > 100 }

    println(s.recursivelyGetChildObjects().size)
}