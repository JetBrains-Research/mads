package org.jetbrains.research.mads_ns.synapses

import org.jetbrains.research.mads.core.types.Response
import org.jetbrains.research.mads.core.types.responses.SignalDoubleChangeResponse
import org.jetbrains.research.mads_ns.hh.HHSignals
import java.lang.Double.max

object SynapseMechanisms {
    val SynapseDecay = Synapse::synapseDecayMechanism
    val STDUpdate = Synapse::STDPWeightUpdateMechanism
}

fun Synapse.synapseDecayMechanism(params: SynapseParameters): List<Response> {
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

fun Synapse.STDPWeightUpdateMechanism(params: SynapseParameters): List<Response> {
    val synapseSignals = this.signals[SynapseSignals::class] as SynapseSignals

    val releaserSig = this.releaser.signals[HHSignals::class] as HHSignals
    val receiverSig = this.receiver.signals[HHSignals::class] as HHSignals

    val weightDelta = 1e-1*((releaserSig.stdpTrace - receiverSig.stdpTrace))
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