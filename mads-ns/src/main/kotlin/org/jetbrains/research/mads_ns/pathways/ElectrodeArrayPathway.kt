package org.jetbrains.research.mads_ns.pathways

import org.jetbrains.research.mads.core.configuration.pathway
import org.jetbrains.research.mads_ns.electrode.ElectrodeArray
import org.jetbrains.research.mads_ns.electrode.ElectrodeArrayMechanisms

fun electrodeArrayPathway() = pathway<ElectrodeArray> {
    mechanism(mechanism = ElectrodeArrayMechanisms.StimuliDynamic) {
        duration = 1000
    }
}