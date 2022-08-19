package org.jetbrains.research.mads_ns.synapses

import org.jetbrains.research.mads.core.types.Response
import org.jetbrains.research.mads.core.types.responses.SignalDoubleChangeResponse

object SynapseMechanisms {
    val SynapseDecay = Synapse::synapseDecayMechanism
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