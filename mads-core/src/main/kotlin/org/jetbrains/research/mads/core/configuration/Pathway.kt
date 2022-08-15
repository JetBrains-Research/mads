package org.jetbrains.research.mads.core.configuration

import org.jetbrains.research.mads.core.types.*

class Pathway<MO : ModelObject> {
    val mocRecords = ArrayList<MocRecord<MO>>()

    fun <MP : MechanismParameters> add(
        mechanism: (MO, MP) -> List<Response>,
        parameters: MP,
        duration: Int,
        condition: (MO) -> Boolean
    ) {
        val mch = applyParametersToMechanism(mechanism, parameters)
        mocRecords.add(MocRecord(mch, duration, condition))
    }
}