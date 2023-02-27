package org.jetbrains.research.mads_ns.pathways

import org.jetbrains.research.mads.core.configuration.pathway
import org.jetbrains.research.mads.core.types.SignalsObject
import org.jetbrains.research.mads_ns.electrode.Electrode
import org.jetbrains.research.mads_ns.electrode.ElectrodeConnection
import org.jetbrains.research.mads_ns.electrode.ElectrodeMechanisms

fun electrodePathway() = pathway<Electrode> {
    mechanism(mechanism = ElectrodeMechanisms.PulseDynamic) {
        duration = 100
    }
}

fun Electrode.connectToCell(cell: SignalsObject) {
    this.connections[ElectrodeConnection] = hashSetOf(cell)
    cell.connections[ElectrodeConnection] = hashSetOf(this)
}