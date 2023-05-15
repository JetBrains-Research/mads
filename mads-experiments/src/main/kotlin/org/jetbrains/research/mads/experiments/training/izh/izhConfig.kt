package org.jetbrains.research.mads.experiments.training.izh

import org.jetbrains.research.mads.core.configuration.Always
import org.jetbrains.research.mads.core.configuration.configure
import org.jetbrains.research.mads.core.configuration.pathway
import org.jetbrains.research.mads.core.types.microsecond
import org.jetbrains.research.mads.core.types.millisecond
import org.jetbrains.research.mads.ns.pathways.spiked
import org.jetbrains.research.mads.ns.physiology.neurons.*
import org.jetbrains.research.mads.ns.physiology.synapses.Synapse
import org.jetbrains.research.mads.ns.physiology.synapses.SynapseCurrentDecayConstants
import org.jetbrains.research.mads.ns.physiology.synapses.SynapseMechanisms
import org.jetbrains.research.mads.ns.physiology.synapses.SynapseSignals

fun trainPhaseConfig() = configure {
    timeResolution = microsecond
    addPathway(pathway<InputNeuron2DGrid> {
        timeResolution = millisecond
        mechanism(mechanism = InputNeuron2DGridMechanisms.GenerateStimuliSpikes) {
            duration = 500
            constants = InputNeuronSpikeRateConstants(35.0)
        }
    })
    addPathway(pathway<InputNeuron> {
        timeResolution = microsecond
        mechanism(mechanism = InputNeuronMechanisms.ProbabilisticSpike) {
            duration = 10_000
        }
        mechanism(mechanism = InputNeuronMechanisms.Silent) {
            duration = 350_000
            condition = {
                val probabilisticSignals = it.signals[ProbabilisticSpikingSignals::class] as ProbabilisticSpikingSignals
                probabilisticSignals.silent == false
            }
        }
//        mechanism(mechanism = NeuronMechanisms.DelayedSpikeTransfer) {
//            duration = 100
//            condition = { it.delayedSpikes.size > 0 }
//            constants = SpikeTransferConstants(I_transfer = 1.0)
//        }
//        mechanism(mechanism = NeuronMechanisms.DelayedSpikeCreation) {
//            duration = 1
//            condition = { spiked(it) }
//        }
        mechanism(mechanism = NeuronMechanisms.SpikeOff) {
            duration = 1
            condition = { spiked(it) }
        }
        // TODO: why only weightUpdate and not normalization?
        mechanism(mechanism = NeuronMechanisms.TripletSTDPWeightUpdate) {
            duration = 1
            condition = { spiked(it) }
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
            duration = 10_000
            condition = {
                val currentSignals = it.signals[CurrentSignals::class] as CurrentSignals
                currentSignals.I_e != 0.0
            }
            constants = SynapseCurrentDecayConstants(
                zeroingLimit = 0.001,
                excitatoryDecayMultiplier = 0.2,
                inhibitoryDecayMultiplier = 0.02
            )
        }
        mechanism(mechanism = SynapseMechanisms.PreDecay) {
            duration = 20_000
        }
        mechanism(mechanism = SynapseMechanisms.Post1Decay) {
            duration = 20_000
        }
        mechanism(mechanism = SynapseMechanisms.Post2Decay) {
            duration = 40_000
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
//        mechanism(mechanism = NeuronMechanisms.DelayedSpikeTransfer) {
//            duration = 100
//            condition = { it.delayedSpikes.size > 0 }
//            constants = SpikeTransferConstants(I_transfer = 1.0)
//        }
//        mechanism(mechanism = NeuronMechanisms.DelayedSpikeCreation) {
//            duration = 1
//            condition = { spiked(it) }
//        }
        mechanism(mechanism = IzhMechanisms.ThetaSpike) {
            duration = 1
            condition = { spiked(it) }
        }
        mechanism(mechanism = IzhMechanisms.ThetaDecay) {
            duration = 10_000_000
        }
        mechanism(mechanism = NeuronMechanisms.TripletSTDPWeightUpdate) {
            duration = 1
            condition = { spiked(it) }
        }
        mechanism(mechanism = NeuronMechanisms.WeightNormalization) {
            duration = 500_000
            condition = { it.weightNormalizationEnabled }
        }
        mechanism(mechanism = NeuronMechanisms.UpdateSpikeCounter) {
            duration = 500_000
        }
    })
}

fun testPhaseConfig() = configure {
    timeResolution = microsecond
    addPathway(pathway<InputNeuron2DGrid> {
        timeResolution = millisecond
        mechanism(mechanism = InputNeuron2DGridMechanisms.GenerateStimuliSpikes) {
            duration = 500
            constants = InputNeuronSpikeRateConstants(35.0)
        }
    })
    addPathway(pathway<InputNeuron> {
        timeResolution = microsecond
        mechanism(mechanism = InputNeuronMechanisms.ProbabilisticSpike) {
            duration = 10_000
        }
        mechanism(mechanism = InputNeuronMechanisms.Silent) {
            duration = 350_000
            condition = {
                val probabilisticSignals = it.signals[ProbabilisticSpikingSignals::class] as ProbabilisticSpikingSignals
                probabilisticSignals.silent == false
            }
        }
//        mechanism(mechanism = NeuronMechanisms.DelayedSpikeTransfer) {
//            duration = 100
//            condition = { it.delayedSpikes.size > 0 }
//            constants = SpikeTransferConstants(I_transfer = 1.0)
//        }
//        mechanism(mechanism = NeuronMechanisms.DelayedSpikeCreation) {
//            duration = 1
//            condition = { spiked(it) }
//        }
        mechanism(mechanism = NeuronMechanisms.SpikeOff) {
            duration = 1
            condition = { spiked(it) }
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
            duration = 10_000
            condition = {
                val currentSignals = it.signals[CurrentSignals::class] as CurrentSignals
                currentSignals.I_e != 0.0
            }
            constants = SynapseCurrentDecayConstants(
                zeroingLimit = 0.001,
                excitatoryDecayMultiplier = 0.2,
                inhibitoryDecayMultiplier = 0.02
            )
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
//        mechanism(mechanism = NeuronMechanisms.DelayedSpikeTransfer) {
//            duration = 100
//            condition = { it.delayedSpikes.size > 0 }
//            constants = SpikeTransferConstants(I_transfer = 1.0)
//        }
//        mechanism(mechanism = NeuronMechanisms.DelayedSpikeCreation) {
//            duration = 1
//            condition = { spiked(it) }
//        }
        mechanism(mechanism = NeuronMechanisms.UpdateSpikeCounter) {
            duration = 500_000
        }
    })
}