package org.jetbrains.research.mads_ns.synapses

import org.jetbrains.research.mads.core.types.MechanismParameters
import org.jetbrains.research.mads.core.types.ModelObject
import org.jetbrains.research.mads.core.types.Response
import org.jetbrains.research.mads.core.types.responses.SignalDoubleChangeResponse
import org.jetbrains.research.mads_ns.physiology.neurons.CurrentChangeResponse
import org.jetbrains.research.mads_ns.physiology.neurons.CurrentSignals
import org.jetbrains.research.mads_ns.physiology.neurons.STDPSignals
import java.lang.Double.max

object SynapseMechanisms {
    val WeightDecay = Synapse::weightDecayMechanism
    val CurrentDecay = Synapse::currentDecay
    val STDUpdate = Synapse::STDPWeightUpdateMechanism
}

data class WeightChangeResponse(
    override val sourceObject: ModelObject,
    override val value: Double,
    override val updateFn: (Double) -> Unit
) : SignalDoubleChangeResponse("${sourceObject.hashCode()}, dWeight, ${value}\n", sourceObject, value, updateFn)

fun Synapse.weightDecayMechanism(params: MechanismParameters): List<Response> {
    val synapseSignals = this.signals[SynapseSignals::class] as SynapseSignals
    val newWeight = synapseSignals.weight * SynapseConstants.weightDecayCoefficient
    val delta = newWeight - synapseSignals.weight

    return arrayListOf(
        WeightChangeResponse(this, delta) { synapseSignals.weight += delta }
    )
}

fun Synapse.currentDecay(params: MechanismParameters): List<Response> {
    val currentSignals = this.signals[CurrentSignals::class] as CurrentSignals
    val delta = -(currentSignals.I_e / 2)

    return arrayListOf(
        CurrentChangeResponse(this, delta) { currentSignals.I_e += delta }
    )
}

fun Synapse.STDPWeightUpdateMechanism(params: MechanismParameters): List<Response> {
    val synapseSignals = this.signals[SynapseSignals::class] as SynapseSignals

    val releaserSig = this.releaser.signals[STDPSignals::class] as STDPSignals
    val receiverSig = this.receiver.signals[STDPSignals::class] as STDPSignals

    var weightDelta = ((releaserSig.stdpTrace - receiverSig.stdpTrace))

    weightDelta *= if (weightDelta < 0) { 0.1 } else { 0.3 }

    val newWeight = max(0.0, synapseSignals.weight + weightDelta)
    val delta = newWeight - synapseSignals.weight

    return arrayListOf(
        WeightChangeResponse(this, delta) { synapseSignals.weight += delta }
    )
}