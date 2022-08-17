package domain

import domain.mechanisms.*
import domain.objects.HHCellObject
import org.jetbrains.research.mads.core.configuration.*
import org.jetbrains.research.mads.core.types.MechanismParameters
import org.jetbrains.research.mads.core.types.ModelObject
import org.jetbrains.research.mads.core.types.Response

//fun hhPathway() = pathway {
//    mechanism(mechanism = HHCellObject::IDynamicMechanism) {
//        parameters(parameters = HHParamsNoSave) withDuration 2 onCondition Always
//    }
//    mechanism(mechanism = HHCellObject::VDynamicMechanism) {
//        parameters(HHParamsSaveToFile) withDuration 2 onCondition Always
//    }
//    mechanism(mechanism = HHCellObject::NDynamicMechanism) {
//        parameters(parameters = HHParamsNoSave) withDuration 2 onCondition Always
//    }
//    mechanism(mechanism = HHCellObject::MDynamicMechanism) {
//        parameters(parameters = HHParamsNoSave) withDuration 2 onCondition Always
//    }
//    mechanism(mechanism = HHCellObject::HDynamicMechanism) {
//        parameters(parameters = HHParamsNoSave) withDuration 2 onCondition Always
//    }
//}

fun <MO : ModelObject, MP : MechanismParameters> cast(fn: (MO, MP)->List<Response>) : (ModelObject, MechanismParameters)->List<Response> {
    return fn as (ModelObject, MechanismParameters) -> List<Response>
}

//fun testPthw() {
//    val builder = MocRecordBuilder()
//    builder.duration = 2
//    builder.mechanism(cast(HHCellObject::IDynamicMechanism))
//    builder.parameters(HHParamsNoSave)
//    builder.build()
//}

fun hhPathway() = pathway {
    mechanism {
        mechanism(mechanism = cast(HHCellObject::IDynamicMechanism))
        parameters(parameters = HHParamsNoSave)
        duration = 2
        build()
    }
    mechanism {
        mechanism(mechanism = cast(HHCellObject::VDynamicMechanism))
        parameters(parameters = HHParamsSaveToFile)
        duration = 2
        build()
    }
    mechanism {
        mechanism(mechanism = cast(HHCellObject::NDynamicMechanism))
        parameters(parameters = HHParamsNoSave)
        duration = 2
        build()
    }
    mechanism {
        mechanism(mechanism = cast(HHCellObject::MDynamicMechanism))
        parameters(parameters = HHParamsNoSave)
        duration = 2
        build()
    }
    mechanism {
        mechanism(mechanism = cast(HHCellObject::HDynamicMechanism))
        parameters(parameters = HHParamsNoSave)
        duration = 2
        build()
    }
}