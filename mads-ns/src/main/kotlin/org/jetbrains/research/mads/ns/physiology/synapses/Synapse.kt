package org.jetbrains.research.mads.ns.physiology.synapses

import org.jetbrains.research.mads.core.types.*
import org.jetbrains.research.mads.ns.physiology.neurons.CurrentSignals
import org.jetbrains.research.mads.ns.physiology.neurons.STDPTripletSignals
import org.jetbrains.research.mads.ns.physiology.neurons.SpikeTransferConstants

object SynapseReleaser : ConnectionType

object SynapseReceiver : ConnectionType

class LearningConstants(val learningRate: Double) : MechanismConstants

class Synapse(
    var releaser: ModelObject,
    var receiver: ModelObject,
    isInhibitory: Boolean = false,
    synapseSignals: SynapseSignals,
    vararg signals: Signals
) : ModelObject(CurrentSignals(I_e = 0.0), synapseSignals, *signals) {

    init {
        val sig = this.signals[SynapseSignals::class] as SynapseSignals
        connections[SynapseReleaser] = hashSetOf(releaser)
        connections[SynapseReceiver] = hashSetOf(receiver)

        if (isInhibitory) {
            sig.synapseSign = -1.0
        }
    }
}

class SynapseSignals(
    weight: Double = 1.0,
    delay: Int = 1,
    val learningEnabled: Boolean = true,
    val maxWeight: Double = 1.0
) : Signals() {
    var weight: Double by observable(weight)
    var synapseSign: Double by observable(1.0)
    var delay: Int by observable(delay)
    var releaserSpiked: Boolean by observable(false)
//    var receiverSpiked: Boolean by observable(false)
}

object SynapseMechanisms {
    val WeightDecay = Synapse::weightDecayMechanism
    val SpikeTransfer = Synapse::spikeTransfer
    val CurrentDecay = Synapse::currentDecay
    val Post1Decay = Synapse::post1Decay
    val Post2Decay = Synapse::post2Decay
    val PreDecay = Synapse::preDecay
    val PreWeightUpdate = Synapse::preWeightUpdate
    val PostWeightUpdate = Synapse::postWeightUpdate
}

@TimeResolution(resolution = millisecond)
@ConstantType(type = DecayConstants::class)
fun Synapse.weightDecayMechanism(params: MechanismParameters): List<Response> {
    val synapseSignals = this.signals[SynapseSignals::class] as SynapseSignals
    val decayConstants = params.constants as DecayConstants
    val delta = signalDecay(synapseSignals.weight, decayConstants, params.dt)

    return arrayListOf(
        this.createResponse {
            synapseSignals.weight += delta
        }
    )
}

@ConstantType(type = SpikeTransferConstants::class)
fun Synapse.spikeTransfer(params: MechanismParameters): List<Response> {
    val iTransfer = (params.constants as SpikeTransferConstants).I_transfer
    val synapseSignals = this.signals[SynapseSignals::class] as SynapseSignals
    val currentSignals = this.signals[CurrentSignals::class] as CurrentSignals
    val receiverCurrentSignals = this.receiver.signals[CurrentSignals::class] as CurrentSignals
    val delta = synapseSignals.weight * synapseSignals.synapseSign * iTransfer

    return listOf(
        this.createResponse {
            currentSignals.I_e += delta
            synapseSignals.releaserSpiked = false
        },
        this.receiver.createResponse {
            receiverCurrentSignals.I_e += delta
        }
    )
}

@TimeResolution(resolution = millisecond)
@ConstantType(type = DecayConstants::class)
fun Synapse.currentDecay(params: MechanismParameters): List<Response> {
    val currentSignals = this.signals[CurrentSignals::class] as CurrentSignals
    val receiverCurrentSignals = this.receiver.signals[CurrentSignals::class] as CurrentSignals
    val decayConstants = params.constants as DecayConstants
    val delta = signalDecay(currentSignals.I_e, decayConstants, params.dt)

    return listOf(
        this.createResponse {
            currentSignals.I_e += delta
        },
        this.receiver.createResponse {
            receiverCurrentSignals.I_e += delta
        }
    )
}

@TimeResolution(resolution = millisecond)
@ConstantType(type = DecayConstants::class)
fun Synapse.preDecay(params: MechanismParameters): List<Response> {
    val stdpSignals = this.signals[STDPTripletSignals::class] as STDPTripletSignals
    val decayConstants = params.constants as DecayConstants
    val delta = signalDecay(stdpSignals.stdpTracePre, decayConstants, params.dt)

    return arrayListOf(
        this.createResponse {
            stdpSignals.stdpTracePre += delta
        }
    )
}

@TimeResolution(resolution = millisecond)
@ConstantType(type = DecayConstants::class)
fun Synapse.post1Decay(params: MechanismParameters): List<Response> {
    val stdpSignals = this.signals[STDPTripletSignals::class] as STDPTripletSignals
    val decayConstants = params.constants as DecayConstants
    val delta = signalDecay(stdpSignals.stdpTracePost1, decayConstants, params.dt)

    return arrayListOf(
        this.createResponse {
            stdpSignals.stdpTracePost1 += delta
        }
    )
}

@TimeResolution(resolution = millisecond)
@ConstantType(type = DecayConstants::class)
fun Synapse.post2Decay(params: MechanismParameters): List<Response> {
    val stdpSignals = this.signals[STDPTripletSignals::class] as STDPTripletSignals
    val decayConstants = params.constants as DecayConstants
    val delta = signalDecay(stdpSignals.stdpTracePost2, decayConstants, params.dt)

    return arrayListOf(
        this.createResponse {
            stdpSignals.stdpTracePost2 += delta
        }
    )
}

// presynaptic
// ge_post += w; pre = 1.; w = clip(w + nu_ee_pre * post1, 0, wmax_ee)
@ExperimentalMechanism
@ConstantType(type = LearningConstants::class)
fun Synapse.preWeightUpdate(params: MechanismParameters): List<Response> {
    val synapseSignals = this.signals[SynapseSignals::class] as SynapseSignals
    return if (synapseSignals.learningEnabled) {
        val stdpSignals = this.signals[STDPTripletSignals::class] as STDPTripletSignals
        val learningRate = (params.constants as LearningConstants).learningRate
        val newWeight = (synapseSignals.weight + learningRate * stdpSignals.stdpTracePost1)
            .coerceIn(0.0, synapseSignals.maxWeight)
        val weightDelta = newWeight - synapseSignals.weight
        val preDelta = 1.0 - stdpSignals.stdpTracePre

        listOf(
            this.createResponse {
                synapseSignals.weight += weightDelta
                stdpSignals.stdpTracePre += preDelta
            }
        )
    } else
        EmptyResponseList
}

// postsynaptic
// post2before = post2; w = clip(w + nu_ee_post * pre * post2before, 0, wmax_ee); post1 = 1.; post2 = 1.
@ExperimentalMechanism
@ConstantType(type = LearningConstants::class)
fun Synapse.postWeightUpdate(params: MechanismParameters): List<Response> {
    val synapseSignals = this.signals[SynapseSignals::class] as SynapseSignals
    return if (synapseSignals.learningEnabled) {
        val stdpSignals = this.signals[STDPTripletSignals::class] as STDPTripletSignals
        val learningRate = (params.constants as LearningConstants).learningRate
        val newWeight = (synapseSignals.weight + learningRate * stdpSignals.stdpTracePre * stdpSignals.stdpTracePost2)
            .coerceIn(0.0, synapseSignals.maxWeight)
        val weightDelta = newWeight - synapseSignals.weight
        val post1Delta = 1.0 - stdpSignals.stdpTracePost1
        val post2Delta = 1.0 - stdpSignals.stdpTracePost2

        return listOf(
            this.createResponse {
                synapseSignals.weight += weightDelta
                stdpSignals.stdpTracePost1 += post1Delta
                stdpSignals.stdpTracePost2 += post2Delta
            }
        )
    } else
        EmptyResponseList
}