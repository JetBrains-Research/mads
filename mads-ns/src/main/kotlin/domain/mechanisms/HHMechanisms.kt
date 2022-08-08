package domain.mechanisms

import domain.Signals
import domain.SimpleParameters
import domain.objects.HHCellObject
import domain.responses.IDynamicResponse
import org.jetbrains.research.mads.core.types.Response

fun HHCellObject.IDynamicMechanism(params: SimpleParameters) : Array<Response>
{
    return arrayOf(IDynamicResponse("Object: " + this.type + "; Signal: I", this, 0))

}

fun HHCellObject.VDynamicMechanism(params: SimpleParameters) : Array<Response>
{
    return arrayOf(domain.responses.VDynamicResponse("Object: " + this.type + "; Signal: V", this, 1))

}

fun HHCellObject.NDynamicMechanism(params: SimpleParameters) : Array<Response>
{
    return arrayOf(domain.responses.NDynamicResponse("Object: " + this.type + "; Signal: N", this, 2))

}

fun HHCellObject.MDynamicMechanism(params: SimpleParameters) : Array<Response>
{
    return arrayOf(domain.responses.MDynamicResponse("Object: " + this.type + "; Signal: M", this, 3))

}

fun HHCellObject.HDynamicMechanism(params: SimpleParameters) : Array<Response>
{
    return arrayOf(domain.responses.HDynamicResponse("Object: " + this.type + "; Signal: H", this, 4))

}

data class HHSignals(val V: Double, val h: Double, val m: Double, val n: Double) : Signals