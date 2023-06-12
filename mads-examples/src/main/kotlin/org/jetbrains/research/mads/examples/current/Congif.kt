package org.jetbrains.research.mads.examples.current

import org.jetbrains.research.mads.core.configuration.Always
import org.jetbrains.research.mads.core.configuration.configure
import org.jetbrains.research.mads.core.configuration.pathway
import org.jetbrains.research.mads.core.types.microsecond
import org.jetbrains.research.mads.ns.overThresholdAndNotSpiked
import org.jetbrains.research.mads.ns.physiology.neurons.*
import org.jetbrains.research.mads.ns.underThresholdAndSpiked

val currentLifConfig = configure {
    timeResolution = microsecond
    addPathway(pathway<LIFNeuron> {
        timeResolution = microsecond
        mechanism(mechanism = LIFMechanisms.VDynamic) {
            duration = 100
            condition = Always
        }
        mechanism(mechanism = NeuronMechanisms.SpikeOn) {
            duration = 100
            condition = { overThresholdAndNotSpiked(it) }
        }
        mechanism(mechanism = NeuronMechanisms.SpikeOff) {
            duration = 100
            condition = { underThresholdAndSpiked(it) }
        }
    })
}

val currentIzhConfig = configure {
    timeResolution = microsecond
    addPathway(pathway<IzhNeuron> {
        timeResolution = microsecond
        mechanism(mechanism = IzhMechanisms.Dynamic) {
            duration = 500
            condition = Always
        }
        mechanism(mechanism = NeuronMechanisms.SpikeOff) {
            duration = 1
            condition = { underThresholdAndSpiked(it) }
        }
    })
}

val currentHHConfig = configure {
    timeResolution = microsecond
    addPathway(pathway<HHNeuron> {
        timeResolution = microsecond
        mechanism(mechanism = HHMechanisms.VDynamic) {
            duration = 25
            condition = Always
        }
        mechanism(mechanism = HHMechanisms.NDynamic) {
            duration = 25
            condition = Always
        }
        mechanism(mechanism = HHMechanisms.MDynamic) {
            duration = 25
            condition = Always
        }
        mechanism(mechanism = HHMechanisms.HDynamic) {
            duration = 25
            condition = Always
        }
        mechanism(mechanism = NeuronMechanisms.SpikeOn) {
            duration = 25
            condition = { overThresholdAndNotSpiked(it) }
        }
        mechanism(mechanism = NeuronMechanisms.SpikeOff) {
            duration = 25
            condition = { underThresholdAndSpiked(it) }
        }
    })
}