package domain

import org.jetbrains.research.mads.core.types.Response

fun SimpleObject.simpleMechanism(params: SimpleParameters) : Array<Response> {
    return arrayOf(SimpleResponse("Object: " + this.type + "; Probability: " + params.probability, this))
}

fun SimpleObject.simpleCondition() : Boolean {
    return this.forCondition
}

fun DummyObject.simpleMechanism(params: SimpleParameters) : Array<Response> {
    return arrayOf(SimpleResponse("Object: " + this.type + "; Probability: " + params.probability, this))
}

fun DummyObject.dummyCondition() : Boolean {
    return this.forCondition
}