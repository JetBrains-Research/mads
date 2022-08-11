package domain.mechanisms

import domain.SimpleParameters
import domain.SimpleResponse
import domain.objects.HHCellObject
import domain.objects.HHSignals
import domain.objects.SynapseObject
import domain.responses.SynapseDecayResponse
import domain.responses.SynapseResponse
import org.jetbrains.research.mads.core.types.Response
import org.jetbrains.research.mads.core.types.SignalsObject
import org.jetbrains.research.mads.core.types.responses.DynamicResponse

fun SynapseObject.spikeTransferMechanism(params: SimpleParameters) : List<Response>
{
    val sourceSignals = objectLeft.signals[HHSignals::class] as HHSignals
    val destSignals = objectRight.signals[HHSignals::class] as HHSignals

    if(sourceSignals.V >= this.spikeThreshold)
    {
        if(!spiked)
        {
            spiked = true

            return arrayListOf(SynapseResponse("spiked", this, weight))
        }
    }
    else
    {
        spiked = false
    }

//    return arrayListOf(SimpleResponse("didnt spike", this))
    return arrayListOf(SynapseResponse("didnt spike", this, 0.0))
}

fun SynapseObject.synapseDecayMechanism(params: SimpleParameters) : List<Response>
{
    return arrayListOf(SynapseDecayResponse("decaying", this))
}