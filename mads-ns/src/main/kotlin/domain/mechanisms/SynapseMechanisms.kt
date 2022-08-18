package domain.mechanisms

import domain.SynapseParameters
import domain.objects.HHSignals
import domain.objects.SynapseObject
import domain.responses.SynapseDecayResponse
import domain.responses.SynapseResponse
import org.jetbrains.research.mads.core.types.Response

object SynapseMechanisms {
    val SpikeTransfer = SynapseObject::spikeTransferMechanism
    val SynapseDecay = SynapseObject::synapseDecayMechanism
}

fun SynapseObject.spikeTransferMechanism(params: SynapseParameters): List<Response> {
    val sourceSignals = objectLeft.signals[HHSignals::class] as HHSignals
//    val destSignals = objectRight.signals[HHSignals::class] as HHSignals

    if (sourceSignals.V >= this.spikeThreshold) {
        if (!spiked) {
            spiked = true

            return arrayListOf(
                SynapseResponse(
                    "spiked",
                    this,
                    params.savingParameters.saver::logResponse,
                    params.savingParameters.saveResponse,
                    weight,
                    true
                )
            )
        }
    } else {
        spiked = false
    }

    return arrayListOf(
        SynapseResponse(
            "didn't spike",
            this,
            params.savingParameters.saver::logResponse,
            params.savingParameters.saveResponse,
            0.0,
            false
        )
    )
}

fun SynapseObject.synapseDecayMechanism(params: SynapseParameters): List<Response> {
    return arrayListOf(
        SynapseDecayResponse(
            "decaying", this, params.savingParameters.saver::logResponse,
            params.savingParameters.saveResponse,
        )
    )
}