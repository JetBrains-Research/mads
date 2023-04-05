package org.jetbrains.research.mads.ns.pathways

import org.jetbrains.research.mads.core.configuration.Always
import org.jetbrains.research.mads.core.configuration.pathway
import org.jetbrains.research.mads.core.types.ModelObject
import org.jetbrains.research.mads.core.types.microsecond
import org.jetbrains.research.mads.core.types.millisecond
import org.jetbrains.research.mads.ns.electrode.Electrode
import org.jetbrains.research.mads.ns.electrode.ElectrodeConnection
import org.jetbrains.research.mads.ns.electrode.ElectrodeMechanisms
import org.jetbrains.research.mads.ns.electrode.PulseConstants

fun electrodePulsePathway() = pathway {
    timeResolution = millisecond
    mechanism(mechanism = ElectrodeMechanisms.PulseDynamic) {
        duration = 10
        condition = Always
        constants = PulseConstants()
    }
}
fun electrodePeriodicPulsePathway() = pathway {
    timeResolution = millisecond
    mechanism(mechanism = ElectrodeMechanisms.PeriodicPulseDynamic) {
        duration = 1
        condition = Always
        constants = PulseConstants()
    }
}

fun electrodeNoisePathway() = pathway {
    timeResolution = microsecond
    mechanism(mechanism = ElectrodeMechanisms.NoiseDynamic) {
        duration = 1000
        condition = Always
    }
}

fun Electrode.connectToCell(cell: ModelObject) {
    this.connections[ElectrodeConnection] = hashSetOf(cell)
    cell.connections[ElectrodeConnection] = hashSetOf(this)
}