package org.jetbrains.research.mads_ns.hh

import org.jetbrains.research.mads.core.telemetry.EmptySaver
import org.jetbrains.research.mads.core.types.Response
import org.jetbrains.research.mads.core.types.SignalsObject
import org.jetbrains.research.mads.core.types.responses.SignalBooleanChangeResponse
import org.jetbrains.research.mads.core.types.responses.SignalDoubleChangeResponse
import org.jetbrains.research.mads_ns.electrode.ElectrodeConnection
import org.jetbrains.research.mads_ns.synapses.*
import java.util.Random
import kotlin.math.exp
import kotlin.math.pow
import kotlin.math.sqrt

object HHMechanisms {
    val IDynamic = HHCell::IDynamic
    val VDynamic = HHCell::VDynamic
    val SpikeTransfer = HHCell::spikeTransfer
    val HDynamic = HHCell::HDynamic
    val NDynamic = HHCell::NDynamic
    val MDynamic = HHCell::MDynamic
    val STDPDecay = HHCell::STDPDecay
}

fun HHCell.STDPDecay(params: HHParameters): List<Response> {
    val signals = this.signals[HHSignals::class] as HHSignals
    val trace = -signals.stdpTrace*(1 - stdpDecayCoefficient)

    val responseString = "${this.hashCode()}, dTrace, ${trace}\n"
//    return arrayListOf(DynamicResponse(responseString, this, delta) { this.signals.I += it })
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

fun HHCell.IDynamic(params: HHParameters): List<Response> {
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
//    return arrayListOf(DynamicResponse(responseString, this, delta) { this.signals.I += it })
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

fun HHCell.VDynamic(params: HHParameters): List<Response> {
    val s = this.signals[HHSignals::class] as HHSignals
    val i = this.signals[CurrentSignals::class] as CurrentSignals
    val c = params.constants as HHConstants

    val IK = c.g_K * s.N.pow(4.0) * (s.V - c.E_K)
    val INa = c.g_Na * s.M.pow(3.0) * s.H * (s.V - c.E_Na)
    val IL = c.g_L * (s.V - c.E_L)

    val delta = ((i.I_e - IK - INa - IL) / c.C_m) * c.dt

    val responseString = "${this.hashCode()}, dV, ${delta}\n"
//    return arrayListOf(DynamicResponse(responseString, this, delta) { this.signals.V += it })
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

fun HHCell.spikeTransfer(params: HHParameters): List<Response> {
    val signals = this.signals[HHSignals::class] as HHSignals
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

fun HHCell.NDynamic(params: HHParameters): List<Response> {
    val signals = this.signals[HHSignals::class] as HHSignals

    val V = signals.V
    val n = signals.N

    val delta = ((AlphaN(V) * (1.0 - n)) - (BetaN(V) * n)) * HHConstants.dt

    val responseString = "${this.hashCode()}, dN, ${delta}\n"
//    return arrayListOf(DynamicResponse(responseString, this, delta) { this.signals.N += it })
    return arrayListOf(
        SignalDoubleChangeResponse(
            responseString,
            this,
            params.savingParameters.saver::logResponse,
            params.savingParameters.saveResponse,
            delta,
            this::updateN
        )
    )
}

fun HHCell.MDynamic(params: HHParameters): List<Response> {
    val signals = this.signals[HHSignals::class] as HHSignals

    val V = signals.V
    val m = signals.M

    val delta = ((AlphaM(V) * (1.0 - m)) - (BetaM(V) * m)) * HHConstants.dt

    val responseString = "${this.hashCode()}, dM, ${delta}\n"
//    return arrayListOf(DynamicResponse(responseString, this, delta) { this.signals.M += it })
    return arrayListOf(
        SignalDoubleChangeResponse(
            responseString,
            this,
            params.savingParameters.saver::logResponse,
            params.savingParameters.saveResponse,
            delta,
            this::updateM
        )
    )
}

fun HHCell.HDynamic(params: HHParameters): List<Response> {
    val signals = this.signals[HHSignals::class] as HHSignals

    val V = signals.V
    val h = signals.H

    val delta = ((AlphaH(V) * (1.0 - h)) - (BetaH(V) * h)) * HHConstants.dt

    val responseString = "${this.hashCode()}, dH, ${delta}\n"
//    return arrayListOf(DynamicResponse(responseString, this, delta) { this.signals.H += it })
    return arrayListOf(
        SignalDoubleChangeResponse(
            responseString,
            this,
            params.savingParameters.saver::logResponse,
            params.savingParameters.saveResponse,
            delta,
            this::updateH
        )
    )
}


private fun AlphaN(V: Double): Double {
    return 0.01 * (V + 55.0) / (1.0 - exp(-0.1 * (V + 55.0)))
}

private fun BetaN(V: Double): Double {
    return 0.125 * exp(-0.0125 * (V + 65.0))
}

private fun AlphaM(V: Double): Double {
    return 0.1 * (V + 40.0) / (1.0 - exp(-0.1 * (V + 40.0)))
}

private fun BetaM(V: Double): Double {
    return 4.0 * exp(-0.0556 * (V + 65.0))
}

private fun AlphaH(V: Double): Double {
    return 0.07 * exp(-0.05 * (V + 65.0))
}

private fun BetaH(V: Double): Double {
    return 1.0 / (1.0 + exp(-0.1 * (V + 35.0)))
}