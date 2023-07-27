package org.jetbrains.research.mads.ns.physiology.neurons

import org.jetbrains.research.mads.core.types.MechanismParameters
import org.jetbrains.research.mads.core.types.Response
import org.jetbrains.research.mads.core.types.Signals
import java.util.*

class InputNeuron(val rnd: Random, vararg signals: Signals) : Neuron(0.0, ProbabilisticSpikingSignals(), *signals)

object InputNeuronMechanisms {
    val ProbabilisticSpike = InputNeuron::probabilisticSpike
    val Silent = InputNeuron::silent
}

@Suppress("UNUSED_PARAMETER")
fun InputNeuron.probabilisticSpike(params: MechanismParameters): List<Response> {
    val probabilisticSpikingSignals = this.signals[ProbabilisticSpikingSignals::class] as ProbabilisticSpikingSignals
    val spikeProbability = probabilisticSpikingSignals.spikeProbability

    val spiked = (rnd.nextDouble() < spikeProbability) && (!probabilisticSpikingSignals.silent)

    if (spiked) {
        val spikeResponses = this.spikesInSynapses()
        val spikesSignals = this.signals[SpikesSignals::class] as SpikesSignals
        return listOf(
            this.createResponse {
                spikesSignals.spiked = true
            },
            *spikeResponses.toTypedArray()
        )
    }

    return standardResponseList
}

@Suppress("UNUSED_PARAMETER")
fun InputNeuron.silent(params: MechanismParameters): List<Response> {
    val probabilisticSpikingSignals = this.signals[ProbabilisticSpikingSignals::class] as ProbabilisticSpikingSignals
    return listOf(
        this.createResponse {
            probabilisticSpikingSignals.silent = true
        }
    )
}