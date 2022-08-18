package domain

import domain.mechanisms.HHMechanisms
import org.jetbrains.research.mads.core.configuration.Always
import org.jetbrains.research.mads.core.configuration.pathway

fun hhPathway() = pathway {
    mechanism(mechanism = HHMechanisms.IDynamic, parameters = HHParamsSaveToFile) {
        duration = 2
        condition = Always
    }
    mechanism(mechanism = HHMechanisms.VDynamic, parameters = HHParamsSaveToFile) {
        duration = 2
        condition = Always
    }
    mechanism(mechanism = HHMechanisms.NDynamic, parameters = HHParamsNoSave) {
        duration = 2
        condition = Always
    }
    mechanism(mechanism = HHMechanisms.MDynamic, parameters = HHParamsNoSave) {
        duration = 2
        condition = Always
    }
    mechanism(mechanism = HHMechanisms.HDynamic, parameters = HHParamsNoSave) {
        duration = 2
        condition = Always
    }
}