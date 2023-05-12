package org.jetbrains.research.mads.ns.physiology.neurons

import org.jetbrains.research.mads.core.types.*
import org.jetbrains.research.mads.ns.ImageProvider
import java.util.*

class CurrentStimuli : Signals() {
    var stimuli: String by observable("")
}

class InputNeuron2DGrid(
    private val provider: ImageProvider,
    val pixelMultiplier: Double = 5.0,
) : ModelObject(CurrentStimuli()) {
    private var neuronsGrid = ArrayList<InputNeuron>()
    private val width = provider.width
    private val height = provider.height

    init {
        for (i in 0 until width) {
            for (j in 0 until height) {
                val rnd = Random(42L + i * j + j)
                val neuron = InputNeuron(rnd, STDPSignals())
                neuron.type = "input_${i}_${j}"
                neuronsGrid.add(neuron)
            }
        }
    }

    fun capacity(): Int {
        return neuronsGrid.size
    }

    fun getNeuron(x: Int, y: Int): InputNeuron {
        val flatCoordinate = y * width + x
        return this[flatCoordinate]
    }

    operator fun get(index: Int): InputNeuron {
        if (index > neuronsGrid.size) {
            val exceptionString = String.format("Trying to get an electrode with bad coordinate!")
            throw RuntimeException(exceptionString)
        }

        return neuronsGrid[index]
    }

    fun getProvider(): ImageProvider {
        return provider
    }

    fun getNeurons(): ArrayList<InputNeuron> {
        return neuronsGrid
    }
}

class InputNeuronSpikeRateConstants(val rate: Double = 35.0) : MechanismConstants

object InputNeuron2DGridMechanisms {
    val GenerateStimuliSpikes = InputNeuron2DGrid::GenerateStimuliSpikes
}

fun InputNeuron2DGrid.transformPixelToSpikes(intensity: Double): Int {
    return (intensity * this.pixelMultiplier).toInt()
}

@ConstantType(InputNeuronSpikeRateConstants::class)
fun InputNeuron2DGrid.GenerateStimuliSpikes(params: MechanismParameters): List<Response> {
    val rate = (params.constants as InputNeuronSpikeRateConstants).rate
    val provider = this.getProvider()
    val img = provider.getNextImage()
    val currentStimuli = this.signals[CurrentStimuli::class] as CurrentStimuli
    val responses: ArrayList<Response> = arrayListOf(
        this.createResponse {
            currentStimuli.stimuli = provider.getImageName()
        }
    )

    for (i in 0 until provider.width) {
        for (j in 0 until provider.height) {
            val grayScaled = (img.getRGB(i, j) and 0xFF) / 255.0
            val numberOfSpikes = this.transformPixelToSpikes(grayScaled)
            val inputNeuron = getNeuron(i, j)
            val spikes = inputNeuron.signals[ProbabilisticSpikingSignals::class] as ProbabilisticSpikingSignals

            responses.add(
                inputNeuron.createResponse {
                    spikes.spikeProbability = numberOfSpikes / rate
                    spikes.silent = false
                }
            )
        }
    }

    return responses
}