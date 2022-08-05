package domain

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