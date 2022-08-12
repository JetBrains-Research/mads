package domain.mechanisms

import domain.DummyObject
import domain.SimpleObject
import domain.SimpleParameters
import domain.SimpleResponse
import org.jetbrains.research.mads.core.telemetry.DataBroker
import org.jetbrains.research.mads.core.types.Response
import org.jetbrains.research.mads.core.types.responses.AddObjectResponse

fun SimpleObject.simpleMechanism(params: SimpleParameters) : List<Response> {
    return arrayListOf(SimpleResponse("Object: " + this.type + "; Probability: " + params.probability, this, DataBroker.INSTANCE::logResponse))
}

fun SimpleObject.simpleAddMechanism(params: SimpleParameters) : List<Response> {
    return if (this.rnd.nextDouble() < params.probability)
        arrayListOf(AddObjectResponse("Object added", this.parent, DataBroker.INSTANCE::logResponse, SimpleObject()))
    else
        arrayListOf(SimpleResponse("False roll", this, DataBroker.INSTANCE::logResponse))
}

fun DummyObject.simpleMechanism(params: SimpleParameters) : List<Response> {
    return arrayListOf(SimpleResponse("Object: " + this.type + "; Probability: " + params.probability, this, DataBroker.INSTANCE::logResponse))
}