package org.jetbrains.research.mads.ns.physiology.synapses

import org.jetbrains.research.mads.core.types.*
import org.jetbrains.research.mads.ns.physiology.neurons.CurrentSignals
import org.jetbrains.research.mads.ns.physiology.neurons.STDPSignals
import kotlin.math.abs

object SynapseReleaser: ConnectionType

object SynapseReceiver: ConnectionType

class WeightDecayConstants(val weightDecayCoefficient: Double = 0.99) : MechanismConstants

class SynapseCurrentDecayConstants(val zeroingLimit: Double = 0.001, val decayMultiplier: Double = 0.5) : MechanismConstants

class Synapse(
    var releaser: ModelObject,
    var receiver: ModelObject,
    isInhibitory: Boolean = false,
    current: CurrentSignals,
    synapse: SynapseSignals
) : ModelObject(current, synapse) {

    init {
        val sig = this.signals[SynapseSignals::class] as SynapseSignals
        connections[SynapseReleaser] = hashSetOf(releaser)
        connections[SynapseReceiver] = hashSetOf(receiver)

        if (isInhibitory) {
            sig.synapseSign = -1.0
        }
    }
}

class SynapseSignals(weight: Double = 1.0) : Signals() {
    var weight: Double by observable(weight)
    var synapseSign: Double by observable(1.0)
}

object SynapseMechanisms {
    val WeightDecay = Synapse::weightDecayMechanism
    val CurrentDecay = Synapse::currentDecay
    val STDUpdate = Synapse::STDPWeightUpdateMechanism
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

@TimeResolution(resolution = millisecond)
@ConstantType(type = SynapseCurrentDecayConstants::class)
fun Synapse.currentDecay(params: MechanismParameters): List<Response> {
    val currentSignals = this.signals[CurrentSignals::class] as CurrentSignals
    val receiverCurrentSignals = this.receiver.signals[CurrentSignals::class] as CurrentSignals
    val zeroingLimit = (params.constants as SynapseCurrentDecayConstants).zeroingLimit
    val decayMultiplier = (params.constants as SynapseCurrentDecayConstants).decayMultiplier

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

fun Synapse.STDPWeightUpdateMechanism(params: MechanismParameters): List<Response> {
    val synapseSignals = this.signals[SynapseSignals::class] as SynapseSignals

    val releaserSig = this.releaser.signals[STDPSignals::class] as STDPSignals
    val receiverSig = this.receiver.signals[STDPSignals::class] as STDPSignals

    var weightDelta = ((releaserSig.stdpTrace - receiverSig.stdpTrace))

    weightDelta *= if (weightDelta < 0) { 0.1 } else { 0.3 }

    if(synapseSignals.synapseSign < 0) {
        weightDelta *= -1
    }

    val newWeight = java.lang.Double.max(0.0, synapseSignals.weight + weightDelta)
    val delta = newWeight - synapseSignals.weight

    return arrayListOf(
        this.createResponse {
            synapseSignals.weight += delta
        }
    )
}

//fun Neuron.tripletStdpWeightUpdateRule(params: MechanismParameters): List<Response> {
//    val synapseSignals = this.signals[SynapseSignals::class] as SynapseSignals
//    val stdpSignals = this.signals[STDPTripletSignals::class] as STDPTripletSignals
//
//    val newWeight = (synapseSignals.weight + 0.0001 * stdpSignals.stdpTracePost1).coerceIn(0.0, 1.0)
//
//    result.add(it.createResponse {
//        synapseSignals.weight = newWeight
//        stdpSignals.stdpTracePre = 1.0
//    })
//}