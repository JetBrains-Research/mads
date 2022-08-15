package domain

import domain.mechanisms.*
import domain.objects.HHCellObject
import domain.objects.HHSignals
import org.jetbrains.research.mads.core.configuration.Configuration
import org.jetbrains.research.mads.core.configuration.Pathway
import org.jetbrains.research.mads.core.simulation.Model
import org.jetbrains.research.mads.core.telemetry.FileSaver
import org.jetbrains.research.mads.core.types.SavingParameters
import org.jetbrains.research.mads.core.types.responses.DynamicResponse

fun main() {
//    createHHCellsExperiment()
//    createDynamicExperimentMultipleI()
    createHHHundredCellsExperiment()
}

fun createHHCellsExperiment() {
    FileSaver.initModelWriters("log/${System.currentTimeMillis()}/", setOf(DynamicResponse::class))

    val I_exp = 8.0
    val dynamic = HHCellObject(HHSignals(I_e = I_exp, V = -65.0, N = 0.32, M = 0.05, H = 0.6))

    val config = Configuration()

    val pathwayDynamic: Pathway<HHCellObject> = Pathway()
    pathwayDynamic.add(
        HHCellObject::IDynamicMechanism,
        HHParameters(SavingParameters(FileSaver, false), HHConstants),
        2
    ) { true }
    pathwayDynamic.add(
        HHCellObject::VDynamicMechanism,
        HHParameters(SavingParameters(FileSaver, true), HHConstants),
        2
    ) { true }
    pathwayDynamic.add(
        HHCellObject::NDynamicMechanism,
        HHParameters(SavingParameters(FileSaver, false), HHConstants),
        2
    ) { true }
    pathwayDynamic.add(
        HHCellObject::MDynamicMechanism,
        HHParameters(SavingParameters(FileSaver, false), HHConstants),
        2
    ) { true }
    pathwayDynamic.add(
        HHCellObject::HDynamicMechanism,
        HHParameters(SavingParameters(FileSaver, false), HHConstants),
        2
    ) { true }

    config.add(HHCellObject::class, arrayListOf(pathwayDynamic))

    val s = Model(arrayListOf(dynamic), config)

    s.simulate { it.currentTime() > 100000 }
    FileSaver.closeModelWriters()
}

fun createDynamicExperimentMultipleI() {
    FileSaver.initModelWriters("log/${System.currentTimeMillis()}/", setOf(DynamicResponse::class))

    for (iv in 2..30 step 1) {
        println(iv)
        val I_exp = iv / 2.0
        val dynamic = HHCellObject(HHSignals(I_e = I_exp, V = -65.0, N = 0.32, M = 0.05, H = 0.6))

        val config = Configuration()

        val pathwayDynamic: Pathway<HHCellObject> = Pathway()
        pathwayDynamic.add(
            HHCellObject::IDynamicMechanism,
            HHParameters(SavingParameters(FileSaver, false), HHConstants),
            2
        ) { true }
        pathwayDynamic.add(
            HHCellObject::VDynamicMechanism,
            HHParameters(SavingParameters(FileSaver, true), HHConstants),
            2
        ) { true }
        pathwayDynamic.add(
            HHCellObject::NDynamicMechanism,
            HHParameters(SavingParameters(FileSaver, false), HHConstants),
            2
        ) { true }
        pathwayDynamic.add(
            HHCellObject::MDynamicMechanism,
            HHParameters(SavingParameters(FileSaver, false), HHConstants),
            2
        ) { true }
        pathwayDynamic.add(
            HHCellObject::HDynamicMechanism,
            HHParameters(SavingParameters(FileSaver, false), HHConstants),
            2
        ) { true }


        config.add(HHCellObject::class, arrayListOf(pathwayDynamic))

        val s = Model(arrayListOf(dynamic), config)
        s.simulate { it.currentTime() > 100_000 }
        FileSaver.closeModelWriters()
    }
}

fun createHHHundredCellsExperiment() {
    FileSaver.initModelWriters("log/${System.currentTimeMillis()}/", setOf(DynamicResponse::class))
    val I_exp = 8.0

    val cells: ArrayList<HHCellObject> = arrayListOf()
    val neuronCount = 10_000
    for (i in 0 until neuronCount) {
        cells.add(HHCellObject(HHSignals(I_e = I_exp, V = -65.0, N = 0.32, M = 0.05, H = 0.6)))
    }

    val config = Configuration()

    val pathwayDynamic: Pathway<HHCellObject> = Pathway()
    pathwayDynamic.add(
        HHCellObject::IDynamicMechanism,
        HHParameters(SavingParameters(FileSaver, false), HHConstants),
        2
    ) { true }
    pathwayDynamic.add(
        HHCellObject::VDynamicMechanism,
        HHParameters(SavingParameters(FileSaver, true), HHConstants),
        2
    ) { true }
    pathwayDynamic.add(
        HHCellObject::NDynamicMechanism,
        HHParameters(SavingParameters(FileSaver, false), HHConstants),
        2
    ) { true }
    pathwayDynamic.add(
        HHCellObject::MDynamicMechanism,
        HHParameters(SavingParameters(FileSaver, false), HHConstants),
        2
    ) { true }
    pathwayDynamic.add(
        HHCellObject::HDynamicMechanism,
        HHParameters(SavingParameters(FileSaver, false), HHConstants),
        2
    ) { true }

    config.add(HHCellObject::class, arrayListOf(pathwayDynamic))

    val s = Model(cells, config)
    s.simulate { it.currentTime() > 10_000 }
    FileSaver.closeModelWriters()
}