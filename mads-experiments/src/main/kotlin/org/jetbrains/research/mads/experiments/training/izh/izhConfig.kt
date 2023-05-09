package org.jetbrains.research.mads.experiments.training.izh

import org.jetbrains.research.mads.core.configuration.Always
import org.jetbrains.research.mads.core.configuration.configure
import org.jetbrains.research.mads.core.configuration.pathway
import org.jetbrains.research.mads.core.types.microsecond
import org.jetbrains.research.mads.core.types.millisecond
import org.jetbrains.research.mads.ns.electrode.*
import org.jetbrains.research.mads.ns.pathways.spiked
import org.jetbrains.research.mads.ns.physiology.neurons.*
import org.jetbrains.research.mads.ns.physiology.synapses.Synapse
import org.jetbrains.research.mads.ns.physiology.synapses.SynapseCurrentDecayConstants
import org.jetbrains.research.mads.ns.physiology.synapses.SynapseMechanisms
import org.jetbrains.research.mads.ns.physiology.synapses.WeightDecayConstants

fun trainPhaseConfig() = configure {
    timeResolution = microsecond
    addPathway(pathway<Electrode> {
        timeResolution = millisecond
        mechanism(mechanism = ElectrodeMechanisms.PulseDynamic) {
            duration = 10
            condition = Always
            constants = PulseConstants()
        }
    })
    addPathway(pathway<Synapse> {
        timeResolution = millisecond
        mechanism(mechanism = SynapseMechanisms.WeightDecay) {
            duration = 100
            constants = WeightDecayConstants()
        }
        mechanism(mechanism = SynapseMechanisms.CurrentDecay) {
            duration = 1
            condition = {
                val currentSignals = it.signals[CurrentSignals::class] as CurrentSignals
                currentSignals.I_e != 0.0
            }
            constants = SynapseCurrentDecayConstants()
        }
        mechanism(mechanism = SynapseMechanisms.STDUpdate) {
            duration = 10
        }
    })
    addPathway(pathway<ElectrodeArray> {
        timeResolution = millisecond
        mechanism(mechanism = ElectrodeArrayMechanisms.StimuliDynamic) {
            duration = 250
            condition = Always
        }
    })
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
            condition = { spiked(it) }
            constants = SpikeTransferConstants(I_transfer = 1.0)
        }
        mechanism(mechanism = NeuronMechanisms.STDPSpike) {
            duration = 1
            condition = { spiked(it) }
        }
        mechanism(mechanism = NeuronMechanisms.STDPDecay) {
            duration = 1000
        }
        mechanism(mechanism = NeuronMechanisms.UpdateSpikeCounter) {
            duration = 250_000
        }
    })
}

fun testPhaseConfig() = configure {
    timeResolution = microsecond
    addPathway(pathway<Electrode> {
        timeResolution = millisecond
        mechanism(mechanism = ElectrodeMechanisms.PulseDynamic) {
            duration = 10
            condition = Always
            constants = PulseConstants()
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
            constants = SynapseCurrentDecayConstants()
        }
    })
    addPathway(pathway<ElectrodeArray> {
        timeResolution = millisecond
        mechanism(mechanism = ElectrodeArrayMechanisms.StimuliDynamic) {
            duration = 250
            condition = Always
        }
    })
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
            condition = { spiked(it) }
            constants = SpikeTransferConstants(I_transfer = 1.0)
        }
        mechanism(mechanism = NeuronMechanisms.STDPSpike) {
            duration = 1
            condition = { spiked(it) }
        }
        mechanism(mechanism = NeuronMechanisms.STDPDecay) {
            duration = 1000
        }
        mechanism(mechanism = NeuronMechanisms.UpdateSpikeCounter) {
            duration = 250_000
        }
    })
}