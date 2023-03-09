package org.jetbrains.research.mads_ns.electrode

import org.jetbrains.research.mads.core.types.MechanismParameters
import org.jetbrains.research.mads.core.types.ModelObject
import org.jetbrains.research.mads.core.types.Response
import org.jetbrains.research.mads_ns.data_provider.ImageProvider
import org.jetbrains.research.mads_ns.physiology.neurons.CurrentSignals
import org.jetbrains.research.mads_ns.physiology.neurons.ProbabilisticSpikingSignals
import org.jetbrains.research.mads_ns.physiology.neurons.SpikesSignals
import kotlin.random.Random


class ElectrodeArray(private val provider: ImageProvider,
                     val pixelMultiplier: Double = 5.0
) : ModelObject() {
    private var electrodesGrid = ArrayList<Electrode>()
    private val width = provider.width
    private val height = provider.height

    init {
        val rnd = Random(42L)

        for (i in 0..width) {
            for (j in 0..height) {
                electrodesGrid.add(Electrode(CurrentSignals(I_e = 0.0), rnd))
            }
        }
    }

    fun getElectrodeByCoordinate(x: Int, y: Int): Electrode {
        val flatCoordinate = y * width + x
        if (flatCoordinate > electrodesGrid.size) {
            val exceptionString = String.format("Trying to get an electrode with bad coordinate!")
            throw RuntimeException(exceptionString)
        }

        return electrodesGrid[flatCoordinate]
    }

    fun getProvider(): ImageProvider {
        return provider
    }

    fun getChildElectrodes(): ArrayList<Electrode> {
        return electrodesGrid
    }
}

object ElectrodeArrayMechanisms {
    val StimuliDynamic = ElectrodeArray::StimuliDynamic
}

fun ElectrodeArray.transformPixelToSpikes(intensity: Double): Int
{
    return (intensity*this.pixelMultiplier).toInt()
}

fun ElectrodeArray.StimuliDynamic(params: MechanismParameters): List<Response> {
    val provider = this.getProvider()
    val img = provider.getNextImage()
    val responses: ArrayList<Response> = arrayListOf()

    for (i in 0 until provider.width) {
        for (j in 0 until provider.height) {
            val grayScaled = (img.getRGB(i, j) and 0xFF) / 255.0
            val numberOfSpikes = this.transformPixelToSpikes(grayScaled)

            val electrode = getElectrodeByCoordinate(i, j)

            val spikes = electrode.signals[ProbabilisticSpikingSignals::class] as ProbabilisticSpikingSignals


            responses.add(
                this.createResponse("nSpikes", numberOfSpikes.toString()) {
                    spikes.spikeProbability = numberOfSpikes / 100.0
                }
            )
        }
    }

    return responses
}
