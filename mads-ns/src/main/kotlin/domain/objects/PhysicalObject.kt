package domain.objects

import domain.SimpleObject
import domain.SimpleResponse
import domain.responses.DynamicResponse
import org.jetbrains.research.mads.core.types.ModelObject
import org.jetbrains.research.mads.core.types.Response

open class PhysicalObject : SimpleObject() {
    override val type = "physical object"
    private val signals = DoubleArray(3)

    init {
        responseMapping[DynamicResponse::class] = ::dynamicResponse
    }

    fun getSignal(signalId: Int) : Double
    {
        return this.signals[signalId]
    }

    fun setSignal(signalId: Int, value: Double)
    {
        this.signals[signalId] = value
    }

    private fun dynamicResponse(response: Response): Array<ModelObject> {
        if (response is DynamicResponse) {
            this.signals[response.signalId] += response.changeValue;
        }

        return arrayOf(this)
    }
}