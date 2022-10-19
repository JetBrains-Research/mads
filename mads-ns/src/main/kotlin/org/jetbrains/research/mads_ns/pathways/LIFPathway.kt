package org.jetbrains.research.mads_ns.pathways

import org.jetbrains.research.mads.core.configuration.Always
import org.jetbrains.research.mads.core.configuration.pathway
import org.jetbrains.research.mads_ns.lif.LIFCell
import org.jetbrains.research.mads_ns.lif.LIFMechanisms
import org.jetbrains.research.mads_ns.lif.LIFParamsNoSave
import org.jetbrains.research.mads_ns.lif.LIFParamsSaveToFile


fun lifPathway() = pathway<LIFCell> {
    mechanism(mechanism = LIFMechanisms.IDynamic, parameters = LIFParamsNoSave) {
        duration = 2
        condition = Always
    }
    mechanism(mechanism = LIFMechanisms.VDynamic, parameters = LIFParamsSaveToFile) {
        duration = 2
        condition = Always
    }
    mechanism(mechanism = LIFMechanisms.SpikeTransfer, parameters = LIFParamsSaveToFile) {
        duration = 2
    }
    mechanism(mechanism = LIFMechanisms.STDPDecay, parameters = LIFParamsNoSave) {
        duration = 10
        condition = Always
    }
}