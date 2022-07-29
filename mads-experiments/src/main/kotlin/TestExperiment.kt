package domain

import org.jetbrains.research.mads.core.configuration.Configuration
import org.jetbrains.research.mads.core.configuration.Pathway
import org.jetbrains.research.mads.core.simulation.Model
import org.jetbrains.research.mads.core.types.ModelObject
import org.jetbrains.research.mads.core.types.parametrize

fun main() {
    val simple = SimpleObject()
    val dummy = DummyObject()

    val config : Configuration<ModelObject> = Configuration()

    val pathwaySimple : Pathway<SimpleObject> = Pathway()
    val pathwayDummy : Pathway<DummyObject> = Pathway()
    pathwaySimple.add(
        parametrize(SimpleObject::simpleMechanism, SimpleParameters(0.5)),
        10, SimpleObject::simpleCondition)
    pathwayDummy.add(
        parametrize(DummyObject::simpleMechanism, SimpleParameters(0.8)),
        10, DummyObject::dummyCondition)

    config.add(SimpleObject::class, arrayListOf(pathwaySimple))
    config.add(DummyObject::class, arrayListOf(pathwayDummy))

    val s = Model(arrayListOf(simple, dummy), config)
    s.simulate()
}