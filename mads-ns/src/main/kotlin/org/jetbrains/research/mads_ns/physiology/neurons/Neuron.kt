package org.jetbrains.research.mads_ns.physiology.neurons

import org.jetbrains.research.mads.core.types.*
import org.jetbrains.research.mads.core.types.responses.SignalBooleanChangeResponse
import org.jetbrains.research.mads.core.types.responses.SignalDoubleChangeResponse
import org.jetbrains.research.mads_ns.electrode.ElectrodeConnection
import org.jetbrains.research.mads_ns.synapses.Synapse
import org.jetbrains.research.mads_ns.synapses.SynapseReceiver
import org.jetbrains.research.mads_ns.synapses.SynapseReleaser
import org.jetbrains.research.mads_ns.synapses.SynapseSignals

open class Neuron(
    spikeThreshold: Double,
    vararg signals: Signals
) : SignalsObject(SpikesSignals(spikeThreshold = spikeThreshold), PotentialSignals(), CurrentSignals(), *signals)

data class SpikesSignals(
    var spiked: Boolean = false,
    var spikeThreshold: Double = 0.0,
) : Signals {
    override fun clone(): Signals {
        return this.copy()
    }
}

data class STDPSignals(
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

data class CurrentChangeResponse(
    override val sourceObject: ModelObject,
    override val value: Double,
    override val updateFn: (Double) -> Unit
) : SignalDoubleChangeResponse("${sourceObject.hashCode()}, dI, ${value}\n", sourceObject, value, updateFn)

data class PotentialChangeResponse(
    override val sourceObject: ModelObject,
    override val value: Double,
    override val updateFn: (Double) -> Unit
) : SignalDoubleChangeResponse("${sourceObject.hashCode()}, dV, ${value}\n", sourceObject, value, updateFn)

data class SpikeOnChangeResponse(
    override val sourceObject: ModelObject,
    override val value: Boolean,
    override val updateFn: (Boolean) -> Unit
) : SignalBooleanChangeResponse("${sourceObject.hashCode()}, +\n", sourceObject, value, updateFn)

data class SpikeOffChangeResponse(
    override val sourceObject: ModelObject,
    override val value: Boolean,
    override val updateFn: (Boolean) -> Unit
) : SignalBooleanChangeResponse("${sourceObject.hashCode()}, -\n", sourceObject, value, updateFn)

data class STDPChangeResponse(
    override val sourceObject: ModelObject,
    override val value: Double,
    override val updateFn: (Double) -> Unit
) : SignalDoubleChangeResponse("${sourceObject.hashCode()}, dTrace, ${value}\n", sourceObject, value, updateFn)

fun Neuron.IDynamic(params: MechanismParameters): List<Response> {
    val currentSignals = this.signals[CurrentSignals::class] as CurrentSignals
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

    val delta = I_e - currentSignals.I_e

    return arrayListOf(
        CurrentChangeResponse(this, delta) { currentSignals.I_e = it }
    )
}

fun Neuron.STDPDecay(params: MechanismParameters): List<Response> {
    val signals = this.signals[STDPSignals::class] as STDPSignals
    val trace = -signals.stdpTrace * (1 - signals.stdpDecayCoefficient)

    return arrayListOf(
        STDPChangeResponse(this, trace) { signals.stdpTrace += it }
    )
}

fun Neuron.STDPSpike(params: MechanismParameters): List<Response> {
    val signals = this.signals[STDPSignals::class] as STDPSignals

    return arrayListOf(
        STDPChangeResponse(this, 1.0) { signals.stdpTrace += it }
    )
}

fun Neuron.spikeOn(params: MechanismParameters): List<Response> {
    val spikesSignals = this.signals[SpikesSignals::class] as SpikesSignals
    return arrayListOf(
        SpikeOnChangeResponse(this, true) { spikesSignals.spiked = it }
    )
}

fun Neuron.spikeOff(params: MechanismParameters): List<Response> {
    val spikesSignals = this.signals[SpikesSignals::class] as SpikesSignals

    return arrayListOf<Response>(
        SpikeOffChangeResponse(this, false) { spikesSignals.spiked = it }
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
                CurrentChangeResponse(it, delta) { I -> currentSignals.I_e = I }
            )
        }
    }

    return result
}