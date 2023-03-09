package org.jetbrains.research.mads_ns.pathways

import org.jetbrains.research.mads.core.configuration.Always
import org.jetbrains.research.mads.core.configuration.pathway
import org.jetbrains.research.mads.core.types.millisecond
import org.jetbrains.research.mads_ns.electrode.ElectrodeArray
import org.jetbrains.research.mads_ns.electrode.ElectrodeArrayMechanisms

fun electrodeArrayPathway() = pathway<ElectrodeArray> {
    timeResolution = millisecond
    mechanism(mechanism = ElectrodeArrayMechanisms.StimuliDynamic) {
        duration = 100
        condition = Always
    }
}