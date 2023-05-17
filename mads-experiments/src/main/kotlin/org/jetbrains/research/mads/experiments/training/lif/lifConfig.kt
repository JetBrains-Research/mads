package org.jetbrains.research.mads.experiments.training.lif

import org.jetbrains.research.mads.core.configuration.Always
import org.jetbrains.research.mads.core.configuration.configure
import org.jetbrains.research.mads.core.configuration.pathway
import org.jetbrains.research.mads.core.types.microsecond
import org.jetbrains.research.mads.core.types.millisecond
import org.jetbrains.research.mads.ns.pathways.spiked
import org.jetbrains.research.mads.ns.physiology.neurons.*
import org.jetbrains.research.mads.ns.physiology.synapses.*
import kotlin.math.abs

fun lifBasicInput() = configure {
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
    addPathway(pathway<AdaptiveLIFNeuron> {
        timeResolution = microsecond
        mechanism(mechanism = AdaptiveLIFMechanisms.VDynamic) {
            duration = 10
            condition = Always
        }
        mechanism(mechanism = NeuronMechanisms.SpikeOff) {
            duration = 1
            condition = { spiked(it) }
        }
        mechanism(mechanism = AdaptiveLIFMechanisms.ThetaSpike) {
            duration = 1
            condition = { spiked(it) && it.adaptiveThreshold }
        }
        mechanism(mechanism = AdaptiveLIFMechanisms.ThetaDecay) {
            duration = 100_000_000
            condition = { it.adaptiveThreshold }
        }
        mechanism(mechanism = NeuronMechanisms.WeightNormalization) {
            duration = 500000 - 1
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
            duration = 3_000
            condition = {
                val stdpSignals = it.signals[STDPTripletSignals::class] as STDPTripletSignals
                abs(stdpSignals.stdpTracePre) >= 0.005
            }
        }
        mechanism(mechanism = SynapseMechanisms.Post1Decay) {
            duration = 3_000
            condition = {
                val stdpSignals = it.signals[STDPTripletSignals::class] as STDPTripletSignals
                abs(stdpSignals.stdpTracePost1) >= 0.005
            }
        }
        mechanism(mechanism = SynapseMechanisms.Post2Decay) {
            duration = 3_000
            condition = {
                val stdpSignals = it.signals[STDPTripletSignals::class] as STDPTripletSignals
                abs(stdpSignals.stdpTracePost2) >= 0.005
            }
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

fun trainPhaseLifConfig() = configure {
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
        mechanism(mechanism = SynapseMechanisms.PreDecay) {
            duration = 20_000
        }
        mechanism(mechanism = SynapseMechanisms.Post1Decay) {
            duration = 20_000
        }
        mechanism(mechanism = SynapseMechanisms.Post2Decay) {
            duration = 40_000
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
    addPathway(pathway<AdaptiveLIFNeuron> {
        timeResolution = microsecond
        mechanism(mechanism = AdaptiveLIFMechanisms.VDynamic) {
            duration = 500
            condition = Always
        }
        mechanism(mechanism = NeuronMechanisms.SpikeOff) {
            duration = 1
            condition = { spiked(it) }
        }
        mechanism(mechanism = AdaptiveLIFMechanisms.ThetaSpike) {
            duration = 1
            condition = { spiked(it) }
        }
        mechanism(mechanism = AdaptiveLIFMechanisms.ThetaDecay) {
            duration = 10_000_000
        }
        mechanism(mechanism = NeuronMechanisms.WeightNormalization) {
            duration = 500_000
            condition = { it.weightNormalizationEnabled }
            constants = WeightNormalizationConstants(coefficient = 35.0)
        }
        mechanism(mechanism = NeuronMechanisms.UpdateSpikeCounter) {
            duration = 500_000
        }
    })
}

fun testPhaseLifConfig() = configure {
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
    addPathway(pathway<AdaptiveLIFNeuron> {
        timeResolution = microsecond
        mechanism(mechanism = AdaptiveLIFMechanisms.VDynamic) {
            duration = 500
            condition = Always
        }
        mechanism(mechanism = NeuronMechanisms.SpikeOff) {
            duration = 1
            condition = { spiked(it) }
        }
        mechanism(mechanism = NeuronMechanisms.UpdateSpikeCounter) {
            duration = 500_000
        }
    })
}