package org.jetbrains.research.experiments

import org.jetbrains.research.mads.core.configuration.configure
import org.jetbrains.research.mads.core.simulation.Model
import org.jetbrains.research.mads.core.telemetry.FileSaver
import org.jetbrains.research.mads.core.types.ModelObject
import org.jetbrains.research.mads.core.types.responses.SignalBooleanChangeResponse
import org.jetbrains.research.mads.core.types.responses.SignalDoubleChangeResponse
import org.jetbrains.research.mads_ns.data_provider.MnistProvider
import org.jetbrains.research.mads_ns.electrode.Electrode
import org.jetbrains.research.mads_ns.electrode.ElectrodeArray
import org.jetbrains.research.mads_ns.hh.CurrentSignals
import org.jetbrains.research.mads_ns.hh.HHCell
import org.jetbrains.research.mads_ns.hh.HHSignals
import org.jetbrains.research.mads_ns.lif.LIFCell
import org.jetbrains.research.mads_ns.lif.LIFConstants
import org.jetbrains.research.mads_ns.lif.LIFSignals
import org.jetbrains.research.mads_ns.pathways.*
import org.jetbrains.research.mads_ns.synapses.Synapse
import org.jetbrains.research.mads_ns.synapses.SynapseSignals
import java.io.File
import javax.imageio.ImageIO
import kotlin.random.Random
import kotlin.system.measureTimeMillis

fun main() {
    createLIFCellsExperiment()
}

fun createLIFCellsExperiment() {
//    FileSaver.initModelWriters("log/${System.currentTimeMillis()}/", setOf(SignalDoubleChangeResponse::class))
    FileSaver.initModelWriters("log/lif_start/", setOf(SignalDoubleChangeResponse::class))
    val rnd: Random = Random(12345L)

    val objects: ArrayList<ModelObject> = arrayListOf()
    val neuronCount = 1
    for (i in 0 until neuronCount) {
        val cell = LIFCell(CurrentSignals(I_e = 0.0), LIFSignals(V = LIFConstants.V_reset))
        val electrode = Electrode(CurrentSignals(I_e = 2.0), rnd)
        electrode.connectToCell(cell)
        objects.add(cell)
    }

    val config = configure {
        addPathway(lifPathway())
    }

    val s = Model(objects, config)
    s.simulate { it.currentTime() > 100_000 }
    FileSaver.closeModelWriters()
}


