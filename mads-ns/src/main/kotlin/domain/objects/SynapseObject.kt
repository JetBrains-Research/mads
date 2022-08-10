package domain.objects

import domain.responses.SynapseResponse
import org.jetbrains.research.mads.core.simulation.Model
import org.jetbrains.research.mads.core.types.Connection
import org.jetbrains.research.mads.core.types.ModelObject
import org.jetbrains.research.mads.core.types.Response
import org.jetbrains.research.mads.core.types.responses.DynamicResponse

class SynapseObject(var objectLeft: HHCellObject,
                    var objectRight: HHCellObject) : ModelObject() {
    var spiked = false
    var weight = 10.0
    val spikeThreshold = 25.0

    init {
        responseMapping[SynapseResponse::class] = ::synapseResponse
    }

    private fun synapseResponse(response: Response): List<ModelObject> {
        if (response is SynapseResponse) {
            objectRight.updateV(response.delta)
        }

        return arrayListOf(this)
    }

}