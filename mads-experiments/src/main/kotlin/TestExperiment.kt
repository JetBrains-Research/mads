package domain

import org.jetbrains.research.mads.core.configuration.Configuration
import org.jetbrains.research.mads.core.configuration.Pathway
import org.jetbrains.research.mads.core.simulation.Model

fun main() {
    val storage = BaseObjectStorage()
    val simple = SimpleObject(storage)
    val dummy = DummyObject(storage)

    val config = Configuration()

    val pathwaySimple: Pathway<SimpleObject> = Pathway()
    val pathwayDummy: Pathway<DummyObject> = Pathway()
    pathwaySimple.add(SimpleObject::simpleMechanism, SimpleParameters(0.5), 10) { it.forCondition }
    pathwaySimple.add(SimpleObject::simpleAddMechanism, SimpleParameters(0.5), 10) { it.forCondition }
    pathwayDummy.add(DummyObject::simpleMechanism, SimpleParameters(0.8), 10) { it.forCondition }

    config.add(SimpleObject::class, arrayListOf(pathwaySimple))
    config.add(DummyObject::class, arrayListOf(pathwayDummy))

    val s = Model(arrayListOf(simple, dummy), config, storage)
    s.init()
    s.simulate { it.currentTime() > 100 }

    println(storage.getObjects().size)
}