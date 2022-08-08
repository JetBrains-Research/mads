package domain

import domain.objects.HHCellObject
import domain.objects.PhysicalObject
import domain.responses.*
import org.jetbrains.research.mads.core.types.responses.AddObjectResponse
import org.jetbrains.research.mads.core.types.Response

fun SimpleObject.simpleMechanism(params: SimpleParameters) : Array<Response> {
    return arrayOf(SimpleResponse("Object: " + this.type + "; Probability: " + params.probability, this))
}

fun SimpleObject.simpleAddMechanism(params: SimpleParameters) : Array<Response> {
    return if (this.rnd.nextDouble() < params.probability)
        arrayOf(AddObjectResponse("Object added", this.parent, SimpleObject()))
    else
        arrayOf(SimpleResponse("False roll", this))
}

fun DummyObject.simpleMechanism(params: SimpleParameters) : Array<Response> {
    return arrayOf(SimpleResponse("Object: " + this.type + "; Probability: " + params.probability, this))
}

fun HHCellObject.IDynamicMechanism(params: SimpleParameters) : Array<Response>
{
    return arrayOf(IDynamicResponse("Object: " + this.type + "; Signal: I", this, 0))

}

fun HHCellObject.VDynamicMechanism(params: SimpleParameters) : Array<Response>
{
    return arrayOf(VDynamicResponse("Object: " + this.type + "; Signal: V", this, 1))

}

fun HHCellObject.NDynamicMechanism(params: SimpleParameters) : Array<Response>
{
    return arrayOf(NDynamicResponse("Object: " + this.type + "; Signal: N", this, 2))

}

fun HHCellObject.MDynamicMechanism(params: SimpleParameters) : Array<Response>
{
    return arrayOf(MDynamicResponse("Object: " + this.type + "; Signal: M", this, 3))

}

fun HHCellObject.HDynamicMechanism(params: SimpleParameters) : Array<Response>
{
    return arrayOf(HDynamicResponse("Object: " + this.type + "; Signal: H", this, 4))

}