package org.jetbrains.research.mads.examples.circuits.izh

import org.jetbrains.research.mads.core.configuration.Always
import org.jetbrains.research.mads.core.configuration.configure
import org.jetbrains.research.mads.core.configuration.pathway
import org.jetbrains.research.mads.core.types.DecayConstants
import org.jetbrains.research.mads.core.types.microsecond
import org.jetbrains.research.mads.ns.physiology.neurons.*
import org.jetbrains.research.mads.ns.physiology.synapses.LearningConstants
import org.jetbrains.research.mads.ns.physiology.synapses.Synapse
import org.jetbrains.research.mads.ns.physiology.synapses.SynapseMechanisms
import org.jetbrains.research.mads.ns.physiology.synapses.SynapseSignals
import org.jetbrains.research.mads.ns.spiked

val simpleCircuitConfigIzh = configure {
    timeResolution = microsecond
    addPathway(pathway<TimerInputNeuron> {
        timeResolution = microsecond
        mechanism(mechanism = TimerInputNeuronMechanisms.Spike) {
            duration = 100_000
            condition = Always
        }
        mechanism(mechanism = NeuronMechanisms.SpikeOff) {
            duration = 1
            condition = { spiked(it) }
        }
    })
    addPathway(pathway<IzhNeuron> {
        timeResolution = microsecond
        mechanism(mechanism = IzhMechanisms.Dynamic) {
            duration = 10
            condition = Always
        }
        mechanism(mechanism = NeuronMechanisms.SpikeOff) {
            duration = 1
            condition = { spiked(it) }
        }
//        mechanism(mechanism = IzhMechanisms.ThetaSpike) {
//            duration = 1
//            condition = { spiked(it) && (it.signals[IzhSignals::class] as IzhSignals).adaptiveThreshold }
//        }
//        mechanism(mechanism = IzhMechanisms.ThetaDecay) {
//            duration = 100_000_000
//            condition = { (it.signals[IzhSignals::class] as IzhSignals).adaptiveThreshold }
//        }
        mechanism(mechanism = NeuronMechanisms.WeightNormalization) {
            duration = 500_000 - 1
            condition = { it.weightNormalizationEnabled }
            // TODO: check
            constants = WeightNormalizationConstants(coefficient = 35.0)
        }
        mechanism(mechanism = NeuronMechanisms.UpdateSpikeCounter) {
            duration = 5_000_000
        }
    })
    addPathway(pathway<Synapse> {
        timeResolution = microsecond
        mechanism(mechanism = SynapseMechanisms.SpikeTransfer) {
            duration = 1
            condition = { (it.signals[SynapseSignals::class] as SynapseSignals).releaserSpiked }
            delay = { (it.signals[SynapseSignals::class] as SynapseSignals).delay }
            constants = SpikeTransferConstants(I_transfer = 1.0)
        }
        mechanism(mechanism = SynapseMechanisms.CurrentDecay) {
            duration = 100
            condition = {
                if (it.type != "syn_in") {
                    false
                } else {
                    val currentSignals = it.signals[CurrentSignals::class] as CurrentSignals
                    currentSignals.I_e != 0.0
                }
            }
            constants = DecayConstants(
                zeroingLimit = 0.005,
                decayMultiplier = 0.5
            )
        }
        mechanism(mechanism = SynapseMechanisms.CurrentDecay) {
            duration = 100
            condition = {
                if (it.type != "syn_in" && it.type != "syn_ei") {
                    false
                } else {
                    val currentSignals = it.signals[CurrentSignals::class] as CurrentSignals
                    currentSignals.I_e != 0.0
                }
            }
            constants = DecayConstants(
                zeroingLimit = 0.005,
                decayMultiplier = 0.525
            )
        }
        mechanism(mechanism = SynapseMechanisms.CurrentDecay) {
            duration = 100
            condition = {
                if (it.type != "syn_ie") {
                    false
                } else {
                    val currentSignals = it.signals[CurrentSignals::class] as CurrentSignals
                    currentSignals.I_e != 0.0
                }
            }
            constants = DecayConstants(
                zeroingLimit = 0.005,
                decayMultiplier = 0.46
            )
        }
        mechanism(mechanism = SynapseMechanisms.PreDecay) {
            duration = 1_000
            condition = {
                val stdpSignals = it.signals[STDPTripletSignals::class] as STDPTripletSignals
                stdpSignals.stdpTracePre != 0.0
            }
            constants = DecayConstants(
                zeroingLimit = 0.005,
                decayMultiplier = 0.05
            )
        }
        mechanism(mechanism = SynapseMechanisms.Post1Decay) {
            duration = 1_000
            condition = {
                val stdpSignals = it.signals[STDPTripletSignals::class] as STDPTripletSignals
                stdpSignals.stdpTracePost1 != 0.0
            }
            constants = DecayConstants(
                zeroingLimit = 0.005,
                decayMultiplier = 0.05
            )
        }
        mechanism(mechanism = SynapseMechanisms.Post2Decay) {
            duration = 1_000
            condition = {
                val stdpSignals = it.signals[STDPTripletSignals::class] as STDPTripletSignals
                stdpSignals.stdpTracePost2 != 0.0
            }
            constants = DecayConstants(
                zeroingLimit = 0.005,
                decayMultiplier = 0.025
            )
        }
        mechanism(mechanism = SynapseMechanisms.PreWeightUpdate) {
            duration = 1
            condition = { (it.releaser.signals[SpikesSignals::class] as SpikesSignals).spiked }
            constants = LearningConstants(learningRate = 0.0001)
        }
        mechanism(mechanism = SynapseMechanisms.PostWeightUpdate) {
            duration = 1
            condition = { (it.receiver.signals[SpikesSignals::class] as SpikesSignals).spiked }
            constants = LearningConstants(learningRate = 0.01)
        }
    })
}