package org.jetbrains.research.mads.core.test_domain

//fun SimpleObject.simpleMechanism(params: SimpleParameters): List<Response> {
//    return arrayListOf(
//        SimpleResponse(
//            "Object: " + this.type + "; Probability: " + params.probability,
//            this,
//            params.savingParameters.saver::logResponse,
//            params.savingParameters.saveResponse
//        )
//    )
//}

//fun SimpleObject.simpleAddMechanism(params: SimpleParameters): List<Response> {
//    return if (this.rnd.nextDouble() < params.probability) {
//        val addedObject = SimpleObject()
//        arrayListOf(
//            this.createResponse("added",addedObject.hashCode().toString()) {
//                this.parent.addObject(addedObject)
//            }
//        )
//    } else
//        arrayListOf()
//}

//fun DummyObject.simpleMechanism(params: SimpleParameters): List<Response> {
//    return arrayListOf(
//        SimpleResponse(
//            "Object: " + this.type + "; Probability: " + params.probability,
//            this,
//            params.savingParameters.saver::logResponse,
//            params.savingParameters.saveResponse
//        )
//    )
//}