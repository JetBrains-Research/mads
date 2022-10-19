package org.jetbrains.research.mads_ns.lif

import org.jetbrains.research.mads.core.telemetry.EmptySaver
import org.jetbrains.research.mads.core.types.Response
import org.jetbrains.research.mads.core.types.SignalsObject
import org.jetbrains.research.mads.core.types.responses.SignalBooleanChangeResponse
import org.jetbrains.research.mads.core.types.responses.SignalDoubleChangeResponse
import org.jetbrains.research.mads_ns.electrode.ElectrodeConnection
import org.jetbrains.research.mads_ns.hh.*
import org.jetbrains.research.mads_ns.synapses.Synapse
import org.jetbrains.research.mads_ns.synapses.SynapseReceiver
import org.jetbrains.research.mads_ns.synapses.SynapseReleaser
import org.jetbrains.research.mads_ns.synapses.SynapseSignals

object LIFMechanisms {
    val IDynamic = LIFCell::IDynamic
    val VDynamic = LIFCell::VDynamic
    val SpikeTransfer = LIFCell::spikeTransfer
    val STDPDecay = LIFCell::STDPDecay
}

fun LIFCell.STDPDecay(params: LIFParameters): List<Response> {
    val signals = this.signals[LIFSignals::class] as LIFSignals
    val trace = -signals.stdpTrace*(1 - stdpDecayCoefficient)

    val responseString = "${this.hashCode()}, dTrace, ${trace}\n"
    return arrayListOf(
            SignalDoubleChangeResponse(
                    responseString,
                    this,
                    params.savingParameters.saver::logResponse,
                    params.savingParameters.saveResponse,
                    trace,
                    this::updateSTDPTrace
            )
    )
}

fun LIFCell.IDynamic(params: LIFParameters): List<Response> {
    var I_e = 0.0

    this.connections[ElectrodeConnection]?.forEach {
        if (it is SignalsObject) {
            val signals = it.signals[CurrentSignals::class] as CurrentSignals
            I_e += signals.I_e
        }
    }
    this.connections[SynapseReceiver]?.forEach {
        if (it is SignalsObject) {
            val signals = it.signals[CurrentSignals::class] as CurrentSignals
            I_e += signals.I_e
        }
    }

    val responseString = "${this.hashCode()}, I_e, ${I_e}\n"
    return arrayListOf(
            SignalDoubleChangeResponse(
                    responseString,
                    this,
                    params.savingParameters.saver::logResponse,
                    params.savingParameters.saveResponse,
                    I_e,
                    this::updateI
            )
    )
}

fun LIFCell.VDynamic(params: LIFParameters): List<Response> {
    val s = this.signals[LIFSignals::class] as LIFSignals
    val i = this.signals[CurrentSignals::class] as CurrentSignals
    val c = params.constants as LIFConstants

    val spiked = (s.V > c.V_thresh)
    val delta = if(spiked) c.V_reset - s.V else (c.E_leak - s.V + (c.Rm * i.I_e)) / c.tau_mem * c.dt

    println(s.V)

    val responseString = "${this.hashCode()}, dV, ${delta}\n"
    return arrayListOf(
            SignalDoubleChangeResponse(
                    responseString,
                    this,
                    params.savingParameters.saver::logResponse,
                    params.savingParameters.saveResponse,
                    delta,
                    this::updateV
            )
    )
}

fun LIFCell.spikeTransfer(params: LIFParameters): List<Response> {
    val signals = this.signals[LIFSignals::class] as LIFSignals
    val result = arrayListOf<Response>()

    if (signals.V >= signals.spikeThreshold && !signals.spiked) {
        this.connections[SynapseReleaser]?.forEach {
            if (it is Synapse) {
                val synapseSignals = it.signals[SynapseSignals::class] as SynapseSignals
                val delta = synapseSignals.weight * synapseSignals.synapseSign * 100.0 // 100.0 – mA
                result.add(
                        SignalDoubleChangeResponse(
                                "${it.hashCode()}, dI, ${delta}\n",
                                it,
                                params.savingParameters.saver::logResponse,
                                params.savingParameters.saveResponse,
                                delta,
                                it::updateI
                        )
                )

            }
        }

        result.add(
                SignalBooleanChangeResponse(
                        "${this.hashCode()}, Spiked\n",
                        this,
                        params.savingParameters.saver::logResponse,
                        params.savingParameters.saveResponse,
                        true,
                        this::updateSpiked
                )
        )

        val traceDelta = 1.0
        result.add(
                SignalDoubleChangeResponse(
                        "${this.hashCode()}, dTrace, ${traceDelta}\n",
                        this,
                        params.savingParameters.saver::logResponse,
                        params.savingParameters.saveResponse,
                        traceDelta,
                        this::updateSTDPTrace
                )
        )

    } else if (signals.V < signals.spikeThreshold && signals.spiked) {
        this.connections[SynapseReleaser]?.forEach {
            if (it is Synapse) {
                val delta = 0.0
                result.add(
                        SignalDoubleChangeResponse(
                                "${it.hashCode()}, dI, ${delta}\n",
                                it,
                                params.savingParameters.saver::logResponse,
                                params.savingParameters.saveResponse,
                                delta,
                                it::updateI
                        )
                )
            }
        }

        result.add(
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

    return result
}