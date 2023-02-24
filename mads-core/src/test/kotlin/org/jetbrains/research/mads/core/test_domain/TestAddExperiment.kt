package org.jetbrains.research.mads.core.test_domain

fun main() {
    createCellsExperiment()
}

fun createCellsExperiment() {
//    val simple = SimpleObject()
//    val dummy = DummyObject()
//
//    val pathwaySimple: Pathway<SimpleObject> =
//        pathway {
////            mechanism(SimpleObject::simpleAddMechanism,
////                SimpleParameters(SkipSaving, EmptyConstants, 0.5)) {
////                duration = 10
////            }
//            mechanism(SimpleObject::simpleAddMechanism) {
//                duration = 10
//            }
//        }
//    val pathwayDummy: Pathway<DummyObject> = pathway {
//        mechanism(DummyObject::dummyMechanism) {
//            duration = 10
//        }
//    }
//
//    val config = configure {
//        addPathway(pathway = pathwaySimple)
//        addPathway(pathway = pathwayDummy)
//    }
//
//    val s = Model(arrayListOf(simple, dummy), config)
//    s.simulate { it.currentTime() > 100 }
//
//    println(s.recursivelyGetChildObjects().size)
}