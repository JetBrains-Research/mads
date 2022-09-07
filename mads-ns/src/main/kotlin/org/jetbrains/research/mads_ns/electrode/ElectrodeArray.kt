package org.jetbrains.research.mads_ns.electrode

import org.jetbrains.research.mads.core.types.ModelObject
import org.jetbrains.research.mads.core.types.Response
import org.jetbrains.research.mads.core.types.responses.SignalBooleanChangeResponse
import org.jetbrains.research.mads.core.types.responses.SignalChangeResponse
import org.jetbrains.research.mads_ns.data_provider.ImageProvider
import org.jetbrains.research.mads_ns.hh.CurrentSignals
import org.jetbrains.research.mads_ns.types.responses.ArrayStimuliResponse
import kotlin.random.Random

class ElectrodeArray(private val provider: ImageProvider,
                     public val pixelMultiplier: Double=5.0) : ModelObject() {
    private var electrodesGrid = ArrayList<Electrode>()
    private val width = provider.width
    private val height = provider.height

    init {
        val rnd: Random = Random(42L)

        for(i in 0 .. width) {
            for(j in 0 .. height) {
                electrodesGrid.add(Electrode(CurrentSignals(I_e = 0.0), rnd))
            }
        }

        responseMapping[ArrayStimuliResponse::class] = ::stimulateCells
    }

    fun stimulateCells(response: Response): List<ModelObject>
    {
        response as ArrayStimuliResponse

        val img = response.stimuliData

        for(i in 0 until width) {
            for(j in 0 until height) {
                val grayScaled = ( img.getRGB(i, j) and 0xFF ) / 255.0
                var electrode = getElectrodeByCoordinate(i, j)
                electrode.updateI(grayScaled * pixelMultiplier)
            }
        }

        return arrayListOf(this)
    }

    fun getElectrodeByCoordinate(x: Int, y: Int) : Electrode
    {
        val flatCoordinate = y*width + x
        if(flatCoordinate > electrodesGrid.size)
        {
            val exceptionString = String.format("Trying to get an electrode with bad coordinate!")
            throw RuntimeException(exceptionString)
        }

        return electrodesGrid[flatCoordinate]
    }

    fun getProvider() : ImageProvider {
        return provider
    }
}
