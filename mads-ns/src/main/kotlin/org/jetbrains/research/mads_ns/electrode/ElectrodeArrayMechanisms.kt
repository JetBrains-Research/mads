package org.jetbrains.research.mads_ns.electrode

import org.jetbrains.research.mads.core.types.Response
import org.jetbrains.research.mads_ns.physiology.neurons.CurrentSignals

object ElectrodeArrayMechanisms {
    val StimuliDynamic = ElectrodeArray::StimuliDynamic
}

fun ElectrodeArray.StimuliDynamic(params: ElectrodeParameters): List<Response> {
    val responseString = "${this.hashCode()}, I, ${1}\n"

    val provider = this.getProvider()
    val img = provider.getNextImage()

    val responses: ArrayList<Response> = arrayListOf()


    for (i in 0 until provider.width) {
        for (j in 0 until provider.height) {
            val grayScaled = (img.getRGB(i, j) and 0xFF) / 255.0
            val electrode = getElectrodeByCoordinate(i, j)
            val current = electrode.signals[CurrentSignals::class] as CurrentSignals
            val delta = grayScaled * this.pixelMultiplier - current.I_e
            responses.add(
                this.createResponse("dI,${delta}") {
                    current.I_e += delta
                }
            )
        }
    }

    return responses
}