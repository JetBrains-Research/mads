package org.jetbrains.research.mads_ns.pathways

import org.jetbrains.research.mads.core.configuration.Always
import org.jetbrains.research.mads.core.configuration.pathway
import org.jetbrains.research.mads_ns.hh.HHCell
import org.jetbrains.research.mads_ns.hh.HHMechanisms
import org.jetbrains.research.mads_ns.hh.HHParamsNoSave
import org.jetbrains.research.mads_ns.hh.HHParamsSaveToFile

fun hhPathway() = pathway<HHCell> {
    mechanism(mechanism = HHMechanisms.IDynamic, parameters = HHParamsSaveToFile) {
        duration = 2
        condition = Always
    }
    mechanism(mechanism = HHMechanisms.VDynamic, parameters = HHParamsSaveToFile) {
        duration = 2
        condition = Always
    }
    mechanism(mechanism = HHMechanisms.SpikeTransfer, parameters = HHParamsSaveToFile) {
        duration = 2
    }
    mechanism(mechanism = HHMechanisms.NDynamic, parameters = HHParamsNoSave) {
        duration = 2
        condition = Always
    }
    mechanism(mechanism = HHMechanisms.MDynamic, parameters = HHParamsNoSave) {
        duration = 2
        condition = Always
    }
    mechanism(mechanism = HHMechanisms.HDynamic, parameters = HHParamsNoSave) {
        duration = 2
        condition = Always
    }
}