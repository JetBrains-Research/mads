package org.jetbrains.research.mads_ns.physiology.neurons

import org.jetbrains.research.mads.core.types.*
import org.jetbrains.research.mads_ns.electrode.ElectrodeConnection
import org.jetbrains.research.mads_ns.physiology.synapses.Synapse
import org.jetbrains.research.mads_ns.physiology.synapses.SynapseReceiver
import org.jetbrains.research.mads_ns.physiology.synapses.SynapseReleaser
import org.jetbrains.research.mads_ns.physiology.synapses.SynapseSignals

class SpikeTransferConstants(val I_transfer: Double = 5.0) : MechanismConstants

abstract class Neuron(
    spikeThreshold: Double = 0.0,
    vararg signals: Signals
) : ModelObject(SpikesSignals(spikeThreshold = spikeThreshold), PotentialSignals(), CurrentSignals(), *signals)

class SpikesSignals(spikeThreshold: Double) : Signals() {
    var spiked: Boolean by observable(false)
    var spikeThreshold: Double by observable(spikeThreshold)
}

class STDPSignals : Signals() {
    var stdpTrace: Double by observable(0.0)
    val stdpDecayCoefficient: Double by observable(0.99)
}

class CurrentSignals(I_e: Double = 0.0) : Signals() {
    var I_e: Double by observable(I_e)
}

class ProbabilisticSpikingSignals(probability: Double = 0.0) : Signals() {
    var spikeProbability: Double by observable(probability)
}

class PotentialSignals : Signals() {
    var V: Double by observable(-65.0)
}

object NeuronMechanisms {
    val IDynamic = Neuron::IDynamic
    val SpikeOn = Neuron::spikeOn
    val SpikeOff = Neuron::spikeOff
    val SpikeTransfer = Neuron::spikeTransfer
    val STDPDecay = Neuron::STDPDecay
    val SpikeDecay = Neuron::spikeDecay
}

fun Neuron.IDynamic(params: MechanismParameters): List<Response> {
    val currentSignals = this.signals[CurrentSignals::class] as CurrentSignals
    var I_e = 0.0

    this.connections[ElectrodeConnection]?.forEach {
        val signals = it.signals[CurrentSignals::class] as CurrentSignals
        I_e += signals.I_e
    }
    this.connections[SynapseReceiver]?.forEach {
        val signals = it.signals[CurrentSignals::class] as CurrentSignals
        I_e += signals.I_e
    }

    val delta = I_e - currentSignals.I_e

    return arrayListOf(
        this.createResponse {
            currentSignals.I_e += delta
        }
    )
}

fun Neuron.STDPDecay(params: MechanismParameters): List<Response> {
    val signals = this.signals[STDPSignals::class] as STDPSignals
    val trace = -signals.stdpTrace * (1 - signals.stdpDecayCoefficient)

    return arrayListOf(
        this.createResponse {
            signals.stdpTrace += trace
        }
    )
}

fun Neuron.STDPSpike(params: MechanismParameters): List<Response> {
    val signals = this.signals[STDPSignals::class] as STDPSignals

    return arrayListOf(
        this.createResponse { // TODO: constant?
            signals.stdpTrace += 1.0
        }
    )
}

fun Neuron.spikeOn(params: MechanismParameters): List<Response> {
    val spikesSignals = this.signals[SpikesSignals::class] as SpikesSignals
    return arrayListOf(
        this.createResponse {
            spikesSignals.spiked = true
        }
    )
}

fun Neuron.spikeOff(params: MechanismParameters): List<Response> {
    val spikesSignals = this.signals[SpikesSignals::class] as SpikesSignals

    return arrayListOf(
        this.createResponse {
            spikesSignals.spiked = false
        }
    )
}

fun Neuron.spikeTransfer(params: MechanismParameters): List<Response> {
    val result = arrayListOf<Response>()

    this.connections[SynapseReleaser]?.forEach {
        if (it is Synapse) {
            val synapseSignals = it.signals[SynapseSignals::class] as SynapseSignals
            val currentSignals = it.signals[CurrentSignals::class] as CurrentSignals
            val delta = synapseSignals.weight * synapseSignals.synapseSign * (params.constants as SpikeTransferConstants).I_transfer // 100.0 – mA
            result.add(
                it.createResponse {
                    currentSignals.I_e += delta
                }
            )
        }
    }

    return result
}

fun Neuron.spikeDecay(params: MechanismParameters): List<Response> {
    val result = arrayListOf<Response>()

    this.connections[SynapseReleaser]?.forEach {
        if (it is Synapse) {
            val currentSignals = it.signals[CurrentSignals::class] as CurrentSignals
            val delta = -currentSignals.I_e*0.1

            result.add(
                    it.createResponse {
                        currentSignals.I_e += delta
                    }
            )
        }
    }

    return result
}