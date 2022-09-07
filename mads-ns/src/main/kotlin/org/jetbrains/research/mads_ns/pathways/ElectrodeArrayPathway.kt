package org.jetbrains.research.mads_ns.pathways

import org.jetbrains.research.mads.core.configuration.pathway
import org.jetbrains.research.mads_ns.electrode.ElectrodeArray
import org.jetbrains.research.mads_ns.electrode.ElectrodeArrayMechanisms
import org.jetbrains.research.mads_ns.electrode.ElectrodeParametersNoSave

fun electrodeArrayPathway() = pathway<ElectrodeArray> {
    mechanism(mechanism = ElectrodeArrayMechanisms.StimuliDynamic, ElectrodeParametersNoSave(0.5, 50.0)) {
        duration = 100
    }
}