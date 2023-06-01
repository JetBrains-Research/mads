package org.jetbrains.research.mads.examples.population

import org.jetbrains.research.mads.core.configuration.Always
import org.jetbrains.research.mads.core.configuration.configure
import org.jetbrains.research.mads.core.configuration.pathway
import org.jetbrains.research.mads.core.types.DecayConstants
import org.jetbrains.research.mads.core.types.microsecond
import org.jetbrains.research.mads.core.types.millisecond
import org.jetbrains.research.mads.ns.electrode.Electrode
import org.jetbrains.research.mads.ns.electrode.ElectrodeMechanisms
import org.jetbrains.research.mads.ns.overThresholdAndNotSpiked
import org.jetbrains.research.mads.ns.spiked
import org.jetbrains.research.mads.ns.physiology.neurons.*
import org.jetbrains.research.mads.ns.physiology.synapses.Synapse
import org.jetbrains.research.mads.ns.physiology.synapses.SynapseMechanisms

val config = configure {
    timeResolution = microsecond
    addPathway(pathway<IzhNeuron> {
        timeResolution = microsecond
        mechanism(mechanism = IzhMechanisms.Dynamic) {
            duration = 500
            condition = Always
        }
        mechanism(mechanism = NeuronMechanisms.SpikeOff) {
            duration = 1
            condition = { spiked(it) }
        }
        mechanism(mechanism = NeuronMechanisms.SpikeTransfer) {
            duration = 1
            condition = { overThresholdAndNotSpiked(it) }
            constants = SpikeTransferConstants(I_transfer = 1.0)
        }
    })
    addPathway(pathway<Electrode> {
        timeResolution = microsecond
        mechanism(mechanism = ElectrodeMechanisms.NoiseDynamic) {
            duration = 1000
            condition = Always
        }
    })
    addPathway(pathway<Synapse> {
        timeResolution = millisecond
        mechanism(mechanism = SynapseMechanisms.CurrentDecay) {
            duration = 1
            condition = {
                val currentSignals = it.signals[CurrentSignals::class] as CurrentSignals
                currentSignals.I_e != 0.0
            }
            constants = DecayConstants()
        }
    })
}