package domain

import domain.mechanisms.*
import domain.objects.HHCellObject
import org.jetbrains.research.mads.core.configuration.pathway
import org.jetbrains.research.mads.core.telemetry.FileSaver
import org.jetbrains.research.mads.core.types.SavingParameters

fun hhPathway() = pathway<HHCellObject> {
    add<HHParameters> {
        mechanism { HHCellObject::IDynamicMechanism }
        parameters { HHParameters(SavingParameters(FileSaver, false), HHConstants) }
        withDuration { 2 }
        onCondition { { true } }
    }
    add<HHParameters> {
        mechanism { HHCellObject::VDynamicMechanism }
        parameters { HHParameters(SavingParameters(FileSaver, true), HHConstants) }
        withDuration { 2 }
        onCondition { { true } }
    }
    add<HHParameters> {
        mechanism { HHCellObject::NDynamicMechanism }
        parameters { HHParameters(SavingParameters(FileSaver, false), HHConstants) }
        withDuration { 2 }
        onCondition { { true } }
    }
    add<HHParameters> {
        mechanism { HHCellObject::MDynamicMechanism }
        parameters { HHParameters(SavingParameters(FileSaver, false), HHConstants) }
        withDuration { 2 }
        onCondition { { true } }
    }
    add<HHParameters> {
        mechanism { HHCellObject::HDynamicMechanism }
        parameters { HHParameters(SavingParameters(FileSaver, false), HHConstants) }
        withDuration { 2 }
        onCondition { { true } }
    }
}