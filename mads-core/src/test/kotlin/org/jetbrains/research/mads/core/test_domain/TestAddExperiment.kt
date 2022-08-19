package org.jetbrains.research.mads.core.test_domain

import org.jetbrains.research.mads.core.configuration.Pathway
import org.jetbrains.research.mads.core.configuration.configure
import org.jetbrains.research.mads.core.configuration.pathway
import org.jetbrains.research.mads.core.simulation.Model
import org.jetbrains.research.mads.core.types.EmptyConstants
import org.jetbrains.research.mads.core.types.SkipSaving

fun main() {
    createCellsExperiment()
}

fun createCellsExperiment() {
    val simple = SimpleObject()
    val dummy = DummyObject()

    val pathwaySimple: Pathway<SimpleObject> =
        pathway {
            mechanism(SimpleObject::simpleMechanism,
                SimpleParameters(SkipSaving, EmptyConstants, 0.5)) {
                duration = 10
            }
            mechanism(SimpleObject::simpleAddMechanism,
                SimpleParameters(SkipSaving, EmptyConstants, 0.5)) {
                duration = 10
            }
        }
    val pathwayDummy: Pathway<DummyObject> = pathway {
        mechanism(DummyObject::simpleMechanism,
            SimpleParameters(SkipSaving, EmptyConstants, 0.8)) {
            duration = 10
        }
    }

    val config = configure {
        addPathway(pathway = pathwaySimple)
        addPathway(pathway = pathwayDummy)
    }

    val s = Model(arrayListOf(simple, dummy), config)
    s.simulate { it.currentTime() > 100 }

    println(s.recursivelyGetChildObjects().size)
}