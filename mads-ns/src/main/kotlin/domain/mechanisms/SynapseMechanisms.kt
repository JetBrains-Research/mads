package domain.mechanisms

import domain.SimpleParameters
import domain.objects.HHSignals
import domain.objects.SynapseObject
import domain.responses.SynapseDecayResponse
import domain.responses.SynapseResponse
import org.jetbrains.research.mads.core.types.Response

fun SynapseObject.spikeTransferMechanism(params: SimpleParameters) : List<Response>
{
    val sourceSignals = objectLeft.signals[HHSignals::class] as HHSignals
//    val destSignals = objectRight.signals[HHSignals::class] as HHSignals

    if(sourceSignals.V >= this.spikeThreshold) {
        if(!spiked)
        {
            spiked = true

            return arrayListOf(SynapseResponse("spiked", this, weight, true))
        }
    }
    else
    {
        spiked = false
    }

    return arrayListOf(SynapseResponse("didn't spike", this, 0.0, false))
}

fun SynapseObject.synapseDecayMechanism(params: SimpleParameters) : List<Response>
{
    return arrayListOf(SynapseDecayResponse("decaying", this))
}