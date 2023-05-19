package org.jetbrains.research.mads.ns.physiology.neurons

import org.jetbrains.research.mads.core.types.*
import org.jetbrains.research.mads.ns.physiology.synapses.Synapse
import org.jetbrains.research.mads.ns.physiology.synapses.SynapseReceiver
import org.jetbrains.research.mads.ns.physiology.synapses.SynapseReleaser
import org.jetbrains.research.mads.ns.physiology.synapses.SynapseSignals

class SpikeTransferConstants(val I_transfer: Double = 5.0) : MechanismConstants

class WeightNormalizationConstants(val coefficient: Double = 35.0) : MechanismConstants

abstract class Neuron(
    spikeThreshold: Double = 0.0,
    vararg signals: Signals
) : ModelObject(SpikesSignals(spikeThreshold = spikeThreshold), PotentialSignals(), CurrentSignals(), *signals)

class SpikesSignals(spikeThreshold: Double) : Signals() {
    var spiked: Boolean by observable(false)
    var spikeThreshold: Double by observable(spikeThreshold)

    var spikeCounterTemp = 0
    var spikeCounter: Int by observable(0)
}

class STDPTripletSignals : Signals() {
    var stdpTracePre: Double by observable(0.0)
    var stdpTracePost1: Double by observable(0.0)
    var stdpTracePost2: Double by observable(0.0)
}

class CurrentSignals(I_e: Double = 0.0) : Signals() {
    var I_e: Double by observable(I_e)
}

class ProbabilisticSpikingSignals(probability: Double = 0.0) : Signals() {
    var spikeProbability: Double by observable(probability)
    var silent: Boolean by observable(false)
}

class PotentialSignals : Signals() {
    var V: Double by observable(-65.0)
}

object NeuronMechanisms {
    val SpikeOn = Neuron::spikeOn
    val SpikeOff = Neuron::spikeOff
    val SpikeTransfer = Neuron::spikeTransfer
    val WeightNormalization = Neuron::weightNormalizationDivisive
    val UpdateSpikeCounter = Neuron::updateSpikeCounter
}

@Suppress("UNUSED_PARAMETER")
fun Neuron.spikeOn(params: MechanismParameters): List<Response> {
    val spikesSignals = this.signals[SpikesSignals::class] as SpikesSignals
    return arrayListOf(
        this.createResponse {
            spikesSignals.spiked = true
            spikesSignals.spikeCounterTemp += 1
        }
    )
}

@Suppress("UNUSED_PARAMETER")
fun Neuron.spikeOff(params: MechanismParameters): List<Response> {
    val spikesSignals = this.signals[SpikesSignals::class] as SpikesSignals

    return arrayListOf(
        this.createResponse {
            spikesSignals.spiked = false
        }
    )
}

@ConstantType(type = SpikeTransferConstants::class)
fun Neuron.spikeTransfer(params: MechanismParameters): List<Response> {
    val result = arrayListOf<Response>()
    val iTransfer = (params.constants as SpikeTransferConstants).I_transfer

    this.connections[SynapseReleaser]?.forEach {
        if (it is Synapse) {
            val synapseSignals = it.signals[SynapseSignals::class] as SynapseSignals
            val currentSignals = it.signals[CurrentSignals::class] as CurrentSignals
            val receiverCurrentSignals = it.receiver.signals[CurrentSignals::class] as CurrentSignals
            val delta = synapseSignals.weight * synapseSignals.synapseSign * iTransfer
            result.add(
                it.createResponse {
                    currentSignals.I_e += delta
                }
            )
            result.add(
                it.receiver.createResponse {
                    receiverCurrentSignals.I_e += delta
                }
            )
        }
    }

    return result
}

fun Neuron.spikesInSynapses(): List<Response> {
    val relSynapses = this.connections[SynapseReleaser]?.map {
        val synapseSignals = (it as Synapse).signals[SynapseSignals::class] as SynapseSignals
        it.createResponse {
            synapseSignals.releaserSpiked = true
        }
    }?.toList() ?: EmptyResponseList
    val recSynapses = this.connections[SynapseReceiver]?.map {
        it.createEmptyResponse()
//        val synapseSignals = (it as Synapse).signals[SynapseSignals::class] as SynapseSignals
//        it.createResponse {
//            synapseSignals.receiverSpiked = true
//        }
    }?.toList() ?: EmptyResponseList

    return relSynapses + recSynapses
}

@Suppress("UNUSED_PARAMETER")
fun Neuron.updateSpikeCounter(params: MechanismParameters): List<Response> {
    val spikesSignals = this.signals[SpikesSignals::class] as SpikesSignals
    return if (spikesSignals.spikeCounterTemp > 0) {
        val newSpikesCount = spikesSignals.spikeCounterTemp

        listOf(
            this.createResponse {
                spikesSignals.spikeCounter = newSpikesCount
                spikesSignals.spikeCounterTemp = 0
            }
        )
    } else
        EmptyResponseList
}

@ExperimentalMechanism
@ConstantType(type = WeightNormalizationConstants::class)
fun Neuron.weightNormalizationDivisive(params: MechanismParameters): List<Response> {
    val weightSum = this.connections[SynapseReceiver]?.filter {
        ((it as Synapse).signals[SynapseSignals::class] as SynapseSignals).learningEnabled
    }?.sumOf {
        ((it as Synapse).signals[SynapseSignals::class] as SynapseSignals).weight
    } ?: 0.0

    return if (weightSum > 0) {
        val wnConst = (params.constants as WeightNormalizationConstants).coefficient
        val coefficient = wnConst / weightSum
        this.connections[SynapseReceiver]?.filter {
            ((it as Synapse).signals[SynapseSignals::class] as SynapseSignals).learningEnabled
        }?.map {
            val synapseSignals = (it as Synapse).signals[SynapseSignals::class] as SynapseSignals
            val newWeight = synapseSignals.weight * coefficient
            this.createResponse { synapseSignals.weight = newWeight }
        }?.toList() ?: EmptyResponseList
    } else
        EmptyResponseList
}