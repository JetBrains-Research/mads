package org.jetbrains.research.mads.ns.physiology.neurons

import org.jetbrains.research.mads.core.types.*
import org.jetbrains.research.mads.ns.physiology.synapses.Synapse
import org.jetbrains.research.mads.ns.physiology.synapses.SynapseReceiver
import org.jetbrains.research.mads.ns.physiology.synapses.SynapseReleaser
import org.jetbrains.research.mads.ns.physiology.synapses.SynapseSignals

class SpikeTransferConstants(val I_transfer: Double = 5.0) : MechanismConstants

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

class STDPSignals : Signals() {
    var stdpTrace: Double by observable(0.0)
    val stdpDecayCoefficient: Double by observable(0.995)
}

class STDPTripletSignals(val stdpDecayCoefficient: Double = 0.99) : Signals() {
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
    val STDPSpike = Neuron::STDPSpike
    val STDPDecay = Neuron::STDPDecay
    val WeightNormalization = Neuron::WeightNormalizationDivisive
    val STDPWeightUpdate = Neuron::stdpWeightUpdateRule
    val TripletSTDPWeightUpdate = Neuron::tripletStdpWeightUpdateRule
    val UpdateSpikeCounter = Neuron::updateSpikeCounter
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
            spikesSignals.spikeCounterTemp += 1
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
    return this.connections[SynapseReleaser]?.map {
        val synapseSignals = (it as Synapse).signals[SynapseSignals::class] as SynapseSignals
        it.createResponse {
            synapseSignals.releaserSpiked = true
        }
    }?.toList() ?: EmptyResponseList
}

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
fun Neuron.WeightNormalizationDivisive(params: MechanismParameters): List<Response> {
    val result = arrayListOf<Response>()

    var weightSum = 0.0

    this.connections[SynapseReceiver]?.forEach {
        if (it is Synapse) {
            val synapseSignals = it.signals[SynapseSignals::class] as SynapseSignals
            weightSum += synapseSignals.weight
        }
    }

    if (weightSum > 0) {
        this.connections[SynapseReceiver]?.forEach {
            if (it is Synapse) {
                val synapseSignals = it.signals[SynapseSignals::class] as SynapseSignals
                result.add(
                    it.createResponse { synapseSignals.weight *= 35.0 / weightSum }
                )
            }
        }
    }

    return result
}

@ExperimentalMechanism
fun Neuron.stdpWeightUpdateRule(params: MechanismParameters): List<Response> {
    val result = arrayListOf<Response>()

    // presynaptic
    // ge_post += w; pre = 1.; w = clip(w + nu_ee_pre * post1, 0, wmax_ee)
    this.connections[SynapseReleaser]?.forEach {
        if (it is Synapse) {
            val stdpSig = it.signals[SynapseSignals::class] as SynapseSignals
            if (stdpSig.learningEnabled) {
                val synapseSignals = it.signals[SynapseSignals::class] as SynapseSignals

                val preStdp = (it.releaser.signals[STDPSignals::class] as STDPSignals).stdpTrace
                val postStdp = (it.receiver.signals[STDPSignals::class] as STDPSignals).stdpTrace
                var weightdelta = -0.001 * (preStdp - postStdp)

                if (synapseSignals.weight + weightdelta < 0) {
                    weightdelta = 0.0
                }

                result.add(it.createResponse { synapseSignals.weight += weightdelta })
            }
        }
    }

    // postsynaptic
    // post2before = post2; w = clip(w + nu_ee_post * pre * post2before, 0, wmax_ee); post1 = 1.; post2 = 1.
    this.connections[SynapseReceiver]?.forEach {
        if (it is Synapse) {
            val stdpSig = it.signals[SynapseSignals::class] as SynapseSignals
            if (stdpSig.learningEnabled) {
                val synapseSignals = it.signals[SynapseSignals::class] as SynapseSignals

                val postStdp = (it.receiver.signals[STDPSignals::class] as STDPSignals).stdpTrace
                val preStdp = (it.releaser.signals[STDPSignals::class] as STDPSignals).stdpTrace

                var weightdelta = 0.01 * (preStdp - postStdp)

                if (synapseSignals.weight + weightdelta < 0) {
                    weightdelta = 0.0
                }

                result.add(it.createResponse { synapseSignals.weight += weightdelta })
            }
        }
    }

    return result
}

@ExperimentalMechanism
fun Neuron.tripletStdpWeightUpdateRule(params: MechanismParameters): List<Response> {
    val result = arrayListOf<Response>()

    // presynaptic
    // ge_post += w; pre = 1.; w = clip(w + nu_ee_pre * post1, 0, wmax_ee)
    this.connections[SynapseReleaser]?.forEach {
        if (it is Synapse) {
            val stdpSig = it.signals[SynapseSignals::class] as SynapseSignals
            if (stdpSig.learningEnabled) {
                val synapseSignals = it.signals[SynapseSignals::class] as SynapseSignals
                val stdpSignals = it.signals[STDPTripletSignals::class] as STDPTripletSignals

                val newWeight = (synapseSignals.weight + 0.0001 * stdpSignals.stdpTracePost1).coerceIn(0.0, 1.0)
                val delta = newWeight - synapseSignals.weight

                result.add(it.createResponse {
                    synapseSignals.weight += delta
                    stdpSignals.stdpTracePre = 1.0
                })
            }
        }
    }

    // postsynaptic
    // post2before = post2; w = clip(w + nu_ee_post * pre * post2before, 0, wmax_ee); post1 = 1.; post2 = 1.
    this.connections[SynapseReceiver]?.forEach {
        if (it is Synapse) {
            val stdpSig = it.signals[SynapseSignals::class] as SynapseSignals
            if (stdpSig.learningEnabled) {
                val synapseSignals = it.signals[SynapseSignals::class] as SynapseSignals
                val stdpSignals = it.signals[STDPTripletSignals::class] as STDPTripletSignals

                val newWeight =
                    (synapseSignals.weight + 0.01 * stdpSignals.stdpTracePre * stdpSignals.stdpTracePost2).coerceIn(
                        0.0,
                        1.0
                    )
                val delta = newWeight - synapseSignals.weight

                result.add(it.createResponse {
                    synapseSignals.weight += delta
                    stdpSignals.stdpTracePost1 = 1.0
                    stdpSignals.stdpTracePost2 = 1.0
                })
            }
        }
    }

    return result
}
