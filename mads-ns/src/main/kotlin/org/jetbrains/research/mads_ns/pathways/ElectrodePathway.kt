package org.jetbrains.research.mads_ns.pathways

import org.jetbrains.research.mads.core.configuration.pathway
import org.jetbrains.research.mads_ns.electrode.Electrode
import org.jetbrains.research.mads_ns.electrode.ElectrodeConnection
import org.jetbrains.research.mads_ns.electrode.ElectrodeMechanisms
import org.jetbrains.research.mads_ns.electrode.ElectrodeParametersNoSave
import org.jetbrains.research.mads_ns.hh.HHCell

fun electrodePathway() = pathway<Electrode> {
    mechanism(mechanism = ElectrodeMechanisms.PulseDynamic, ElectrodeParametersNoSave(0.5, 50.0)) {
        duration = 100
    }
}

fun Electrode.connectToCell(cell: HHCell) {
    this.connections[ElectrodeConnection] = hashSetOf(cell)
    cell.connections[ElectrodeConnection] = hashSetOf(this)
}