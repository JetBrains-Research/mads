package org.jetbrains.research.mads.core.test_domain

fun main() {
    createDynamicExperiment()
}

fun createDynamicExperiment() {
//    val dynamic = DynamicObject(DynSignals())
//
//    val pathwayDynamic: Pathway<DynamicObject> =
//        pathway {
//            mechanism(mechanism = DynamicObject::simpleDynamicMechanism) {
//                duration = 10
//            }
//        }
//
//    val config = configure {
//        addPathway(pathway = pathwayDynamic)
//    }
//
//    val s = Model(arrayListOf(dynamic), config)
//    s.simulate { it.currentTime() > 100 }
//
//    println((dynamic.signals[DynSignals::class] as DynSignals).x)
}