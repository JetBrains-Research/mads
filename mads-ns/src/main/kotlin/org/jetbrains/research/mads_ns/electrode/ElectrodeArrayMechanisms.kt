package org.jetbrains.research.mads_ns.electrode

import org.jetbrains.research.mads.core.types.Response
import org.jetbrains.research.mads.core.types.responses.SignalDoubleChangeResponse
import org.jetbrains.research.mads_ns.types.responses.ArrayStimuliResponse

object ElectrodeArrayMechanisms {
    val StimuliDynamic = ElectrodeArray::StimuliDynamic
}

fun ElectrodeArray.StimuliDynamic(params: ElectrodeParameters): List<Response> {
    val responseString = "${this.hashCode()}, I, ${1}\n"

    val provider = this.getProvider()
    val img = provider.getNextImage()

    return arrayListOf(
            ArrayStimuliResponse(
                    responseString,
                    this,
                    params.savingParameters.saver::logResponse,
                    params.savingParameters.saveResponse,
                    img
            )
    )
}