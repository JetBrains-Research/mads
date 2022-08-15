package org.jetbrains.research.mads.core.configuration

import org.jetbrains.research.mads.core.types.*

data class MocRecord<MO : ModelObject>(
    val mechanism: ((MO) -> List<Response>),
    val duration: Int,
    val condition: ((MO) -> Boolean)
)

class MocRecordBuilder<MO : ModelObject, MP : MechanismParameters> {
    private var mechanism: ((MO, MP) -> List<Response>) = fun(mo: MO, mp: MP): List<Response> { return arrayListOf() }

    @Suppress("UNCHECKED_CAST")
    private var parameters: MP = EmptyParameters as MP
    private var duration: Int = 0
    private var condition: ((MO) -> Boolean) = fun(mo: MO): Boolean { return true }

    fun mechanism(lambda: () -> ((MO, MP) -> List<Response>)) {
        this.mechanism = lambda()
    }

    fun parameters(lambda: () -> MP) {
        this.parameters = lambda()
    }

    fun withDuration(lambda: () -> Int) {
        this.duration = lambda()
    }

    fun onCondition(lambda: () -> ((MO) -> Boolean)) {
        this.condition = lambda()
    }

    fun build() = MocRecord(applyParametersToMechanism(mechanism, parameters), duration, condition)
}
