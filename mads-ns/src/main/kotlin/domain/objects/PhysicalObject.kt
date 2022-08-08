package domain.objects

import domain.Signals
import domain.SimpleObject
import domain.SimpleResponse
import domain.responses.DynamicResponse
import org.jetbrains.research.mads.core.types.ModelObject
import org.jetbrains.research.mads.core.types.Response
import java.util.logging.Logger

open class PhysicalObject(open val signals: Signals) : SimpleObject() {
    override val type = "physical object"

    val history = mutableListOf<Signals>()

    init {
        responseMapping[DynamicResponse::class] = ::dynamicResponse
    }

    private fun dynamicResponse(response: Response): Array<ModelObject> {
        if (response is DynamicResponse) {
            response.updateFn(response.delta)

//            if(this.signals is HHSignals) {
//                var signalsCopy = HHSignals((this.signals as HHSignals).I, (this.signals as HHSignals).V, (this.signals as HHSignals).N, (this.signals as HHSignals).M, (this.signals as HHSignals).H)

//                history.add(signalsCopy)
//            }
        }

        return arrayOf(this)
    }
}