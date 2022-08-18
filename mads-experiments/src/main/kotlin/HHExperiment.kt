package domain

import domain.objects.HHCellObject
import domain.objects.HHSignals
import org.jetbrains.research.mads.core.configuration.Configuration
import org.jetbrains.research.mads.core.configuration.configure
import org.jetbrains.research.mads.core.simulation.Model
import org.jetbrains.research.mads.core.telemetry.FileSaver
import org.jetbrains.research.mads.core.types.responses.DynamicResponse

fun main() {
    createHHCellsExperiment()
}

fun createHHCellsExperiment() {
    FileSaver.initModelWriters("log/${System.currentTimeMillis()}/", setOf(DynamicResponse::class))
    val I_exp = 8.0

    val cells: ArrayList<HHCellObject> = arrayListOf()
    val neuronCount = 10_000
    for (i in 0 until neuronCount) {
        cells.add(HHCellObject(HHSignals(I_e = I_exp, V = -65.0, N = 0.32, M = 0.05, H = 0.6)))
    }

    val config = configure {
        addPathway(hhPathway())
    }

    val s = Model(cells, config)
    s.simulate { it.currentTime() > 10_000 }
    FileSaver.closeModelWriters()
}