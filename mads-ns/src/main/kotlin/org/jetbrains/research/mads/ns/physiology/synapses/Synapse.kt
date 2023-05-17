package org.jetbrains.research.mads.ns.physiology.synapses

import org.jetbrains.research.mads.core.types.*
import org.jetbrains.research.mads.ns.physiology.neurons.CurrentSignals
import org.jetbrains.research.mads.ns.physiology.neurons.STDPTripletSignals
import org.jetbrains.research.mads.ns.physiology.neurons.SpikeTransferConstants
import kotlin.math.abs

object SynapseReleaser : ConnectionType

object SynapseReceiver : ConnectionType

class WeightDecayConstants(val weightDecayCoefficient: Double = 0.99) : MechanismConstants

class SynapseCurrentDecayConstants(
    val zeroingLimit: Double = 0.001,
    val excitatoryDecayMultiplier: Double = 0.5,
    val inhibitoryDecayMultiplier: Double = 0.5
) : MechanismConstants {
    constructor(zeroingLimit: Double = 0.001, decayMultiplier: Double = 0.5) : this(
        zeroingLimit,
        decayMultiplier,
        decayMultiplier
    )
}

class LearningConstants(val learningRate: Double) : MechanismConstants

class Synapse(
    var releaser: ModelObject,
    var receiver: ModelObject,
    isInhibitory: Boolean = false,
    current: CurrentSignals,
    synapse: SynapseSignals,
    vararg signals: Signals
) : ModelObject(current, synapse, *signals) {

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

fun Synapse.weightDecayMechanism(params: MechanismParameters): List<Response> {
    val synapseSignals = this.signals[SynapseSignals::class] as SynapseSignals
    val newWeight = synapseSignals.weight * (params.constants as WeightDecayConstants).weightDecayCoefficient
    val delta = newWeight - synapseSignals.weight

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
@ConstantType(type = SynapseCurrentDecayConstants::class)
fun Synapse.currentDecay(params: MechanismParameters): List<Response> {
    val synapseSignals = this.signals[SynapseSignals::class] as SynapseSignals
    val currentSignals = this.signals[CurrentSignals::class] as CurrentSignals
    val receiverCurrentSignals = this.receiver.signals[CurrentSignals::class] as CurrentSignals
    val zeroingLimit = (params.constants as SynapseCurrentDecayConstants).zeroingLimit
    val decayMultiplier =
        if (synapseSignals.synapseSign > 0) (params.constants as SynapseCurrentDecayConstants).excitatoryDecayMultiplier
        else (params.constants as SynapseCurrentDecayConstants).inhibitoryDecayMultiplier

    val delta =
        if (abs(currentSignals.I_e) <= zeroingLimit) {
            -currentSignals.I_e
        } else {
            -decayMultiplier * currentSignals.I_e * params.dt
        }

    return listOf(
        this.createResponse {
            currentSignals.I_e += delta
        },
        this.receiver.createResponse {
            receiverCurrentSignals.I_e += delta
        }
    )
}

@Suppress("UNUSED_PARAMETER")
fun Synapse.preDecay(params: MechanismParameters): List<Response> {
    val stdpSignals = this.signals[STDPTripletSignals::class] as STDPTripletSignals
    val delta = -0.2 * stdpSignals.stdpTracePre

    return arrayListOf(
        this.createResponse {
            stdpSignals.stdpTracePre += delta
        }
    )
}

@Suppress("UNUSED_PARAMETER")
fun Synapse.post1Decay(params: MechanismParameters): List<Response> {
    val stdpSignals = this.signals[STDPTripletSignals::class] as STDPTripletSignals
    val delta = -0.2 * stdpSignals.stdpTracePost1


    return arrayListOf(
        this.createResponse {
            stdpSignals.stdpTracePost1 += delta
        }
    )
}

@Suppress("UNUSED_PARAMETER")
fun Synapse.post2Decay(params: MechanismParameters): List<Response> {
    val stdpSignals = this.signals[STDPTripletSignals::class] as STDPTripletSignals
    val delta = -0.1 * stdpSignals.stdpTracePost2


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
            .coerceIn(0.0, 1.0)
        val delta = newWeight - synapseSignals.weight

        listOf(
            this.createResponse {
                synapseSignals.weight += delta
                stdpSignals.stdpTracePre = 1.0
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
            .coerceIn(0.0, 1.0)
        val delta = newWeight - synapseSignals.weight

        return listOf(
            this.createResponse {
                synapseSignals.weight += delta
                stdpSignals.stdpTracePost1 = 1.0
                stdpSignals.stdpTracePost2 = 1.0
            }
        )
    } else
        EmptyResponseList
}