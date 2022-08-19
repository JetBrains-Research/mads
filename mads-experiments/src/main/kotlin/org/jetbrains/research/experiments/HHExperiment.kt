package org.jetbrains.research.experiments

import org.jetbrains.research.mads.core.configuration.configure
import org.jetbrains.research.mads.core.simulation.Model
import org.jetbrains.research.mads.core.telemetry.FileSaver
import org.jetbrains.research.mads.core.types.ModelObject
import org.jetbrains.research.mads.core.types.responses.SignalBooleanChangeResponse
import org.jetbrains.research.mads.core.types.responses.SignalDoubleChangeResponse
import org.jetbrains.research.mads_ns.electrode.Electrode
import org.jetbrains.research.mads_ns.hh.CurrentSignals
import org.jetbrains.research.mads_ns.hh.HHCell
import org.jetbrains.research.mads_ns.hh.HHSignals
import org.jetbrains.research.mads_ns.pathways.electrodePathway
import org.jetbrains.research.mads_ns.pathways.hhPathway
import org.jetbrains.research.mads_ns.pathways.synapsePathway
import org.jetbrains.research.mads_ns.synapses.SynapseObject
import org.jetbrains.research.mads_ns.synapses.SynapseSignals
import kotlin.random.Random
import kotlin.system.measureTimeMillis

fun main() {
    createHHCellsExperiment()
//    createSynapseExperiment()
//    createTwoPopulationsExperiment()
}

fun createHHCellsExperiment() {
    FileSaver.initModelWriters("log/${System.currentTimeMillis()}/", setOf(SignalDoubleChangeResponse::class))
    val I_exp = 8.0
    val rnd: Random = Random(12345L)

    val objects: ArrayList<ModelObject> = arrayListOf()
    val neuronCount = 1
    for (i in 0 until neuronCount) {
        val cell = HHCell(CurrentSignals(I_e = 0.0), HHSignals(V = -65.0, N = 0.32, M = 0.05, H = 0.6))
        val electrode = Electrode(CurrentSignals(I_e = I_exp), rnd)
        electrode.connectToHHCell(cell)
        objects.add(cell)
        objects.add(electrode)
    }

    val config = configure {
        addPathway(electrodePathway())
        addPathway(hhPathway())
    }

    val s = Model(objects, config)
    s.simulate { it.currentTime() > 10_000 }
    FileSaver.closeModelWriters()
}

fun createSynapseExperiment() {
    FileSaver.initModelWriters("log/${System.currentTimeMillis()}/", setOf(SignalDoubleChangeResponse::class, SignalBooleanChangeResponse::class))
    val I_exp = 8.0

    val cellSource = HHCell(CurrentSignals(I_e = I_exp), HHSignals(V = -65.0, N = 0.32, M = 0.05, H = 0.6))
    val cellDest = HHCell(CurrentSignals(I_e = I_exp), HHSignals(V = -65.0, N = 0.32, M = 0.05, H = 0.6))

    val synapse = SynapseObject(cellSource, cellDest, false, SynapseSignals())

    val config = configure {
        addPathway(hhPathway())
        addPathway(synapsePathway())
    }
    val s = Model(arrayListOf(cellSource, cellDest, synapse), config)

    val elapsed = measureTimeMillis {
        s.simulate { it.currentTime() > 100_000 }
    }

    FileSaver.closeModelWriters()
    println("Time taken: $elapsed")
    println("Already calculated")
}

fun createTwoPopulationsExperiment() {
    FileSaver.initModelWriters("log/${System.currentTimeMillis()}/", setOf(SignalDoubleChangeResponse::class, SignalBooleanChangeResponse::class))

    val I_exp = 8.0
    val excCount = 80
    val inhCount = 20

    val excCells: ArrayList<HHCell> = arrayListOf()

    for (i in 0 until excCount) {
        excCells.add(
            HHCell(
                CurrentSignals(I_e = I_exp),
                HHSignals(V = Random.nextDouble(-65.0, 0.0), N = 0.32, M = 0.05, H = 0.6),
                constantCurrent = false
            )
        )
    }

    val inhibCells: ArrayList<HHCell> = arrayListOf()

    for (i in 0 until inhCount) {
        inhibCells.add(
            HHCell(
                CurrentSignals(I_e = 5.0),
                HHSignals(V = Random.nextDouble(-65.0, 0.0), N = 0.32, M = 0.05, H = 0.6),
                constantCurrent = false
            )
        )
    }

    val synapses: ArrayList<SynapseObject> = arrayListOf()

    for (i in 0 until excCount) {
        for (j in 0 until excCount) {
            if (i != j) {
                val synapse = SynapseObject(excCells[i], excCells[j],false, SynapseSignals())
                synapses.add(synapse)
            }
        }
    }

    for (i in 0 until excCount) {
        for (j in 0 until inhCount) {
            val synapse = SynapseObject(excCells[i], inhibCells[j],false, SynapseSignals())
            synapses.add(synapse)
        }
    }

    for (i in 0 until inhCount) {
        for (j in 0 until inhCount) {
            if (i != j) {
                val synapse = SynapseObject(inhibCells[i], inhibCells[j], isInhibitory = true, SynapseSignals())
                synapses.add(synapse)
            }
        }
    }

//    for(i in 0 until inhCount)
//    {
//        for(j in 0 until excCount)
//        {
//            val synapse = SynapseObject(inhibCells[i], excCells[j], isInhibitory = true)
//            synapses.add(synapse)
//        }
//    }

    val allObjects: ArrayList<ModelObject> = arrayListOf()
    allObjects.addAll(inhibCells)
    allObjects.addAll(excCells)
    allObjects.addAll(synapses)

    val config = configure {
        addPathway(hhPathway())
        addPathway(synapsePathway())
    }

    val s = Model(allObjects, config)

    val elapsed = measureTimeMillis {
        s.simulate { it.currentTime() > 25_000 }
    }
    FileSaver.closeModelWriters()
    println("Time taken: $elapsed")
    println("Already calculated")
}