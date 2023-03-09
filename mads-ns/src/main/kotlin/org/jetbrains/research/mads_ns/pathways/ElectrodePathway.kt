package org.jetbrains.research.mads_ns.pathways

import org.jetbrains.research.mads.core.configuration.Always
import org.jetbrains.research.mads.core.configuration.pathway
import org.jetbrains.research.mads.core.telemetry.FileSaver
import org.jetbrains.research.mads.core.types.ModelObject
import org.jetbrains.research.mads.core.types.millisecond
import org.jetbrains.research.mads_ns.electrode.Electrode
import org.jetbrains.research.mads_ns.electrode.ElectrodeConnection
import org.jetbrains.research.mads_ns.electrode.ElectrodeMechanisms

fun electrodePathway() = pathway {
    timeResolution = millisecond
    mechanism(mechanism = ElectrodeMechanisms.PulseDynamic) {
        duration = 10
        condition = Always
        logFn = FileSaver::logResponse
    }
}

fun Electrode.connectToCell(cell: ModelObject) {
    this.connections[ElectrodeConnection] = hashSetOf(cell)
    cell.connections[ElectrodeConnection] = hashSetOf(this)
}