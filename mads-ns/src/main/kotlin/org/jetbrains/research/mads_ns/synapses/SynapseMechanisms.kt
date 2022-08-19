package org.jetbrains.research.mads_ns.synapses

import org.jetbrains.research.mads.core.telemetry.EmptySaver
import org.jetbrains.research.mads_ns.hh.HHSignals
import org.jetbrains.research.mads.core.types.Response
import org.jetbrains.research.mads.core.types.responses.SignalBooleanChangeResponse
import org.jetbrains.research.mads.core.types.responses.SignalDoubleChangeResponse

object SynapseMechanisms {
    val SpikeTransfer = SynapseObject::spikeTransferMechanism
    val SynapseDecay = SynapseObject::synapseDecayMechanism
}

fun SynapseObject.spikeTransferMechanism(params: SynapseParameters): List<Response> {
    val synapseSignals = this.signals[SynapseSignals::class] as SynapseSignals
    val sourceHHSignals = objectLeft.signals[HHSignals::class] as HHSignals
//    val destSignals = objectRight.signals[HHSignals::class] as HHSignals

    if (sourceHHSignals.V >= synapseSignals.spikeThreshold) {
        if (!synapseSignals.spiked) {
            val delta = synapseSignals.weight * synapseSignals.synapseSign
            val spiked = true
            return arrayListOf(
                SignalBooleanChangeResponse(
                    "${this.hashCode()}, +\n",
                    this,
                    params.savingParameters.saver::logResponse,
                    params.savingParameters.saveResponse,
                    spiked,
                    this::updateSpiked
                ),
                SignalDoubleChangeResponse(
                    "${objectRight.hashCode()}, dI_ext, ${delta}\n",
                    this,
                    params.savingParameters.saver::logResponse,
                    params.savingParameters.saveResponse,
                    delta,
                    objectRight::updateIexternal
                )
            )
        }
    }

    return arrayListOf(
        SignalBooleanChangeResponse(
            response = "${this.hashCode()}, -\n",
            sourceObject = this,
            EmptySaver::logResponse,            // We don't save any responses from + to -
            logResponse = false,
            value = false,
            this::updateSpiked
        )
    )
}

fun SynapseObject.synapseDecayMechanism(params: SynapseParameters): List<Response> {
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