package org.jetbrains.research.mads_ns.physiology.neurons

import kotlinx.serialization.Serializable
import org.jetbrains.research.mads.core.types.MechanismParameters
import org.jetbrains.research.mads.core.types.ModelObject
import org.jetbrains.research.mads.core.types.Response
import org.jetbrains.research.mads.core.types.Signals
import org.jetbrains.research.mads_ns.electrode.ElectrodeConnection
import org.jetbrains.research.mads_ns.physiology.synapses.Synapse
import org.jetbrains.research.mads_ns.physiology.synapses.SynapseReceiver
import org.jetbrains.research.mads_ns.physiology.synapses.SynapseReleaser
import org.jetbrains.research.mads_ns.physiology.synapses.SynapseSignals

abstract class Neuron(
    spikeThreshold: Double,
    vararg signals: Signals
) : ModelObject(SpikesSignals(spikeThreshold = spikeThreshold), PotentialSignals(), CurrentSignals(), *signals)

@Serializable
data class SpikesSignals(
    var spiked: Boolean = false,
    var spikeThreshold: Double = 0.0,
) : Signals {
    override fun clone(): Signals {
        return this.copy()
    }
}

@Serializable
data class STDPSignals(
    var stdpTrace: Double = 0.0,
    val stdpDecayCoefficient: Double = 0.99
) : Signals {
    override fun clone(): Signals {
        return this.copy()
    }
}

@Serializable
data class CurrentSignals(
    var I_e: Double = 0.0,
) : Signals {
    override fun clone(): Signals {
        return this.copy()
    }
}

@Serializable
data class PotentialSignals(
    var V: Double = -65.0,
) : Signals {
    override fun clone(): Signals {
        return this.copy()
    }
}

object NeuronMechanisms {
    val IDynamic = Neuron::IDynamic
    val SpikeOn = Neuron::spikeOn
    val SpikeOff = Neuron::spikeOff
    val SpikeTransfer = Neuron::spikeTransfer
    val STDPDecay = Neuron::STDPDecay
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
        this.createResponse("dI",delta.toString()) {
            currentSignals.I_e += delta
        }
    )
}

fun Neuron.STDPDecay(params: MechanismParameters): List<Response> {
    val signals = this.signals[STDPSignals::class] as STDPSignals
    val trace = -signals.stdpTrace * (1 - signals.stdpDecayCoefficient)

    return arrayListOf(
        this.createResponse("dTrace",trace.toString()) {
            signals.stdpTrace += trace
        }
    )
}

fun Neuron.STDPSpike(params: MechanismParameters): List<Response> {
    val signals = this.signals[STDPSignals::class] as STDPSignals

    return arrayListOf(
        this.createResponse("dTrace","1.0"){ // TODO: constant?
            signals.stdpTrace += 1.0
        }
    )
}

fun Neuron.spikeOn(params: MechanismParameters): List<Response> {
    val spikesSignals = this.signals[SpikesSignals::class] as SpikesSignals
    return arrayListOf(
        this.createResponse("spike","+") {
            spikesSignals.spiked = true
        }
    )
}

fun Neuron.spikeOff(params: MechanismParameters): List<Response> {
    val spikesSignals = this.signals[SpikesSignals::class] as SpikesSignals

    return arrayListOf(
        this.createResponse("spike","-") {
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
            val delta = synapseSignals.weight * synapseSignals.synapseSign * 100.0 // 100.0 – mA
            result.add(
                it.createResponse("dI",delta.toString()) {
                    currentSignals.I_e += delta
                }
            )
        }
    }

    return result
}