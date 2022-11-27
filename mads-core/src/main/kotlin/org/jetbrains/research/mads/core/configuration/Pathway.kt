package org.jetbrains.research.mads.core.configuration

import org.jetbrains.research.mads.core.types.EmptyParameters
import org.jetbrains.research.mads.core.types.MechanismParameters
import org.jetbrains.research.mads.core.types.ModelObject
import org.jetbrains.research.mads.core.types.Response
import kotlin.reflect.KClass

class Pathway<MO : ModelObject>(val type: KClass<MO>) {
    val configuredMechanisms = ArrayList<ConfiguredMechanism<MO>>()

    fun <MP : MechanismParameters> mechanism(
        mechanism: (MO, MP) -> List<Response>,
        parameters: MP,
        lambda: MocRecordBuilder<MO, MP>.() -> Unit
    ) {
        configuredMechanisms.add(MocRecordBuilder(mechanism, parameters).apply(lambda).build())
    }

    @Suppress("UNCHECKED_CAST")
    fun <MP : MechanismParameters> mechanism(
        mechanism: (MO, MP) -> List<Response>,
        lambda: MocRecordBuilder<MO, MP>.() -> Unit
    ) {
        configuredMechanisms.add(MocRecordBuilder(mechanism, EmptyParameters as MP).apply(lambda).build())
    }

    companion object {
        inline operator fun <reified MO : ModelObject> invoke(): Pathway<MO> = Pathway(MO::class)
    }
}

inline fun <reified MO : ModelObject> pathway(init: Pathway<MO>.() -> Unit): Pathway<MO> {
    val pathway: Pathway<MO> = Pathway()
    pathway.init()
    return pathway
}