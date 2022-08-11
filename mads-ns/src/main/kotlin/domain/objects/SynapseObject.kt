package domain.objects

import domain.responses.SynapseDecayResponse
import domain.responses.SynapseResponse
import org.jetbrains.research.mads.core.simulation.Model
import org.jetbrains.research.mads.core.types.Connection
import org.jetbrains.research.mads.core.types.ModelObject
import org.jetbrains.research.mads.core.types.Response
import org.jetbrains.research.mads.core.types.responses.DynamicResponse
import kotlin.math.sqrt

class SynapseObject(var objectLeft: HHCellObject,
                    var objectRight: HHCellObject,
                    var weight: Double = 1.0,
                    private val spikeWeight: Double = 1.0,
                    private val isInhibitory: Boolean=false) : ModelObject() {
    var spiked = false
    val spikeThreshold = 25.0

    private val weightDecayCoefficient = 0.99
    private var synapseSign = 1.0

    init {
        responseMapping[SynapseResponse::class] = ::synapseResponse
        responseMapping[SynapseDecayResponse::class] = ::weightDecayResponse

        if(isInhibitory)
        {
            synapseSign = -1.0
        }
    }

    private fun synapseResponse(response: Response): List<ModelObject> {
        if (response is SynapseResponse && response.spiked) {
//            weight = sqrt(weight + spikeWeight)
//            println(response.delta*synapseSign)

            objectRight.updateIexternal(response.delta*synapseSign)
        }

        return arrayListOf(this)
    }

    private fun weightDecayResponse(response: Response): List<ModelObject> {
        if (response is SynapseDecayResponse) {
            weight *= weightDecayCoefficient
        }

        return arrayListOf(this)
    }
}