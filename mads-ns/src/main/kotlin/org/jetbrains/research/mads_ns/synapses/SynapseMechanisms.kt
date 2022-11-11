package org.jetbrains.research.mads_ns.synapses

import org.jetbrains.research.mads.core.types.Response
import org.jetbrains.research.mads.core.types.responses.SignalDoubleChangeResponse
import org.jetbrains.research.mads_ns.physiology.neurons.CurrentSignals
import org.jetbrains.research.mads_ns.physiology.neurons.SpikesSignals
import java.lang.Double.max

object SynapseMechanisms {
    val WeightDecay = Synapse::weightDecayMechanism
    val CurrentDecay = Synapse::currentDecay
    val STDUpdate = Synapse::STDPWeightUpdateMechanism
}

fun Synapse.weightDecayMechanism(params: SynapseParameters): List<Response> {
    val synapseSignals = this.signals[SynapseSignals::class] as SynapseSignals
    val newWeight = synapseSignals.weight * SynapseConstants.weightDecayCoefficient

    return arrayListOf(
        SignalDoubleChangeResponse(
            "${this.hashCode()}, weight, ${newWeight}\n",
            this,
            params.savingParameters.saver::logResponse,
            params.savingParameters.saveResponse,
            newWeight,
            this::updateWeight
        )
    )
}

fun Synapse.currentDecay(params: SynapseParameters): List<Response> {
    val currentSignals = this.signals[CurrentSignals::class] as CurrentSignals
    val delta = -(currentSignals.I_e / 2)

    return arrayListOf(SignalDoubleChangeResponse(
        response = "${this.hashCode()}, dI_e, ${delta}\n",
        sourceObject = this,
        params.savingParameters.saver::logResponse,
        logResponse = params.savingParameters.saveResponse,
        value = delta
    ) {
        currentSignals.I_e += delta
    })
}

fun Synapse.STDPWeightUpdateMechanism(params: SynapseParameters): List<Response> {
    val synapseSignals = this.signals[SynapseSignals::class] as SynapseSignals

    val releaserSig = this.releaser.signals[SpikesSignals::class] as SpikesSignals
    val receiverSig = this.receiver.signals[SpikesSignals::class] as SpikesSignals

    var weightDelta = ((releaserSig.stdpTrace - receiverSig.stdpTrace))

    if (weightDelta < 0) {
        weightDelta *= 0.1
    } else {
        weightDelta *= 0.3
    }

    val newWeight = max(0.0, synapseSignals.weight + weightDelta)

    return arrayListOf(
        SignalDoubleChangeResponse(
            "${this.hashCode()}, dWeight_stdp, ${newWeight}\n",
            this,
            params.savingParameters.saver::logResponse,
            params.savingParameters.saveResponse,
            newWeight,
            this::updateWeight
        )
    )
}