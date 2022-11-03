package org.jetbrains.research.mads_ns.physiology.neurons

import org.jetbrains.research.mads.core.telemetry.EmptySaver
import org.jetbrains.research.mads.core.types.Response
import org.jetbrains.research.mads.core.types.Signals
import org.jetbrains.research.mads.core.types.SignalsObject
import org.jetbrains.research.mads.core.types.responses.SignalBooleanChangeResponse
import org.jetbrains.research.mads.core.types.responses.SignalDoubleChangeResponse
import org.jetbrains.research.mads_ns.electrode.ElectrodeConnection
import org.jetbrains.research.mads_ns.physiology.neurons.hh.HHParameters
import org.jetbrains.research.mads_ns.physiology.neurons.hh.HHSignals
import org.jetbrains.research.mads_ns.synapses.Synapse
import org.jetbrains.research.mads_ns.synapses.SynapseReceiver
import org.jetbrains.research.mads_ns.synapses.SynapseReleaser
import org.jetbrains.research.mads_ns.synapses.SynapseSignals

open class Neuron(
    spikes: SpikesSignals,
    current: CurrentSignals,
    vararg signals: Signals
) : SignalsObject(spikes, current, *signals)

data class SpikesSignals(
    var spiked: Boolean = false,
    var spikeThreshold: Double = 25.0,
    var stdpTrace: Double = 0.0,
    val stdpDecayCoefficient: Double = 0.99
) : Signals {
    override fun clone(): Signals {
        return this.copy()
    }
}

data class CurrentSignals(
    var I_e: Double = 0.0,
) : Signals {
    override fun clone(): Signals {
        return this.copy()
    }
}

object NeuronMechanisms {
    val IDynamic = Neuron::IDynamic
    val SpikeTransfer = Neuron::spikeTransfer
    val STDPDecay = Neuron::STDPDecay
}

fun Neuron.IDynamic(params: HHParameters): List<Response> {
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
            I_e
        ) {
            val sig = this.signals[CurrentSignals::class] as CurrentSignals
            sig.I_e = it
        }
    )
}

fun Neuron.STDPDecay(params: HHParameters): List<Response> {
    val signals = this.signals[SpikesSignals::class] as SpikesSignals
    val trace = -signals.stdpTrace*(1 - signals.stdpDecayCoefficient)

    val responseString = "${this.hashCode()}, dTrace, ${trace}\n"
    return arrayListOf(
        SignalDoubleChangeResponse(
            responseString,
            this,
            params.savingParameters.saver::logResponse,
            params.savingParameters.saveResponse,
            trace
        ) {
            val sig = this.signals[SpikesSignals::class] as SpikesSignals
            sig.stdpTrace += it
        }
    )
}

fun Neuron.spikeTransfer(params: HHParameters): List<Response> {
    val hhSignals = this.signals[HHSignals::class] as HHSignals
    val spikesSignals = this.signals[SpikesSignals::class] as SpikesSignals
    val currentSignals = this.signals[CurrentSignals::class] as CurrentSignals
    val result = arrayListOf<Response>()

    if (hhSignals.V >= spikesSignals.spikeThreshold && !spikesSignals.spiked) {
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
                        delta
                    ) { I ->
                        currentSignals.I_e = I
                    }
                )

            }
        }

        result.add(
            SignalBooleanChangeResponse(
                "${this.hashCode()}, Spiked\n",
                this,
                params.savingParameters.saver::logResponse,
                params.savingParameters.saveResponse,
                true
            ) {
                spikesSignals.spiked = it
            }
        )

        val traceDelta = 1.0
        result.add(
            SignalDoubleChangeResponse(
                "${this.hashCode()}, dTrace, ${traceDelta}\n",
                this,
                params.savingParameters.saver::logResponse,
                params.savingParameters.saveResponse,
                traceDelta
            ) {
                spikesSignals.stdpTrace += it
            }
        )

    } else if (hhSignals.V < spikesSignals.spikeThreshold && spikesSignals.spiked) {
        this.connections[SynapseReleaser]?.forEach {
            if (it is Synapse) {
                val delta = 0.0
                result.add(
                    SignalDoubleChangeResponse(
                        "${it.hashCode()}, dI, ${delta}\n",
                        it,
                        params.savingParameters.saver::logResponse,
                        params.savingParameters.saveResponse,
                        delta
                    ) { I ->
                        currentSignals.I_e = I
                    }
                )
            }
        }

        result.add(
            SignalBooleanChangeResponse(
                response = "${this.hashCode()}, -\n",
                sourceObject = this,
                EmptySaver::logResponse,            // We don't save any responses from + to -
                logResponse = false,
                value = false
            ) {
                spikesSignals.spiked = it
            }
        )
    }

    return result
}