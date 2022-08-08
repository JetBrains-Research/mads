package domain.mechanisms

import domain.DummyObject
import domain.SimpleObject
import domain.SimpleParameters
import domain.SimpleResponse
import org.jetbrains.research.mads.core.types.Response
import org.jetbrains.research.mads.core.types.responses.AddObjectResponse

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