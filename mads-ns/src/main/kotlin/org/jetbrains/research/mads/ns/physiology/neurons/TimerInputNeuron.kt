package org.jetbrains.research.mads.ns.physiology.neurons

import org.jetbrains.research.mads.core.types.MechanismParameters
import org.jetbrains.research.mads.core.types.Response
import org.jetbrains.research.mads.core.types.Signals

class TimerInputNeuron(vararg signals: Signals) : Neuron(0.0, ProbabilisticSpikingSignals(), *signals)

object TimerInputNeuronMechanisms {
    val Spike = TimerInputNeuron::spike
}

@Suppress("UNUSED_PARAMETER")
fun TimerInputNeuron.spike(params: MechanismParameters): List<Response> {
    val spikeResponses = this.spikesInSynapses()
    val spikesSignals = this.signals[SpikesSignals::class] as SpikesSignals
    return listOf(
        this.createResponse {
            spikesSignals.spiked = true
        },
        *spikeResponses.toTypedArray()
    )
}