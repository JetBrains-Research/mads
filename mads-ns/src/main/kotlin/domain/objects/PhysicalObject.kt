package domain.objects

import domain.Signals
import domain.SimpleObject
import domain.SimpleResponse
import domain.responses.DynamicResponse
import org.jetbrains.research.mads.core.types.ModelObject
import org.jetbrains.research.mads.core.types.Response

open class PhysicalObject(open val signals: Signals) : SimpleObject() {
    override val type = "physical object"

    init {
        responseMapping[DynamicResponse::class] = ::dynamicResponse
    }

    private fun dynamicResponse(response: Response): Array<ModelObject> {
        if (response is DynamicResponse) {
            println(String.format("Changing a signal with Id %d", response.signalId))
        }

        return arrayOf(this)
    }
}