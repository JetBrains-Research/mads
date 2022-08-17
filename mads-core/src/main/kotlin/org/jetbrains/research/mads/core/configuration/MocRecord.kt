package org.jetbrains.research.mads.core.configuration

import org.jetbrains.research.mads.core.types.*
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

data class MocRecord<MO : ModelObject>(
    val mechanism: ((MO) -> List<Response>),
    val duration: Int,
    val condition: ((MO) -> Boolean)
)

//class MocRecordBuilder<MO : ModelObject, MP : MechanismParameters>(private val mechanism: ((MO, MP) -> List<Response>)) {
//    @Suppress("UNCHECKED_CAST")
//    private var parameters: MP = EmptyParameters as MP
//    private var duration: Int = 1
//    private var condition: ((MO) -> Boolean) = fun(mo: MO): Boolean { return true }
//
//    fun parameters(parameters: MP): MocRecordBuilder<MO, MP> {
//        this.parameters = parameters
//        return this
//    }
//
//    infix fun withDuration(duration: Int): MocRecordBuilder<MO, MP> {
//        this.duration = duration
//        return this
//    }
//
//    infix fun onCondition(condition: ((MO) -> Boolean)) {
//        this.condition = condition
//    }
//
//    fun build() = MocRecord(applyParametersToMechanism(mechanism, parameters), duration, condition)
//}

typealias Predicate<T> = ((T) -> Boolean)

object Always : Predicate<ModelObject> {
    override fun invoke(p1: ModelObject): Boolean {
        return true
    }
}


sealed class MocRecordBuilder() {
    var duration: Int = 1
    var condition: ((ModelObject) -> Boolean) = fun(mo: ModelObject): Boolean { return true }

    interface Functioned {
        var mechanism: (ModelObject, MechanismParameters) -> List<Response>
    }

    interface Parametrized {
        var parameters: MechanismParameters
    }

    private class Impl : MocRecordBuilder(), Functioned, Parametrized {
        override lateinit var mechanism: (ModelObject, MechanismParameters) -> List<Response>
        override lateinit var parameters: MechanismParameters
    }

    companion object {
        operator fun invoke(): MocRecordBuilder =
            Impl()
    }
}

// For each required property create an extension setter
@OptIn(ExperimentalContracts::class)
fun MocRecordBuilder.mechanism(mechanism: (ModelObject, MechanismParameters) -> List<Response>) {
    contract {
        // In the setter contract specify that after setter invocation the builder can be smart-casted to the corresponding interface type
        returns() implies (this@mechanism is MocRecordBuilder.Functioned)
    }
    // To set the property, you need to cast the builder to the type of the interface corresponding to the property
    // The cast is safe since the only subclass of `sealed class PersonBuilder` implements all such interfaces
    (this as MocRecordBuilder.Functioned).mechanism = mechanism
}

@OptIn(ExperimentalContracts::class)
fun MocRecordBuilder.parameters(parameters: MechanismParameters) {
    contract {
        // In the setter contract specify that after setter invocation the builder can be smart-casted to the corresponding interface type
        returns() implies (this@parameters is MocRecordBuilder.Parametrized)
    }
    // To set the property, you need to cast the builder to the type of the interface corresponding to the property
    // The cast is safe since the only subclass of `sealed class PersonBuilder` implements all such interfaces
    (this as MocRecordBuilder.Parametrized).parameters = parameters
}

// Create an extension build function that can only be called on builders that can be smart-casted to all the interfaces corresponding to required properties
// If you forget to put any of these interface into where-clause compiler won't allow you to use corresponding property in the function body
fun <S> S.build(): MocRecord<ModelObject>
        where S : MocRecordBuilder,
              S : MocRecordBuilder.Functioned,
              S : MocRecordBuilder.Parametrized =
//    MocRecord(applyParametersToMechanism(
//        (this as MocRecordBuilder.Functioned<MO, MP>).mechanism,
//        (this as MocRecordBuilder.Parametrized<MO, MP>).parameters), duration, condition)
        MocRecord(applyParametersToMechanism(mechanism, parameters), duration, condition)