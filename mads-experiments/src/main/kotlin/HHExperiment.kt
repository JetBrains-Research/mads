package domain

import domain.mechanisms.SynapseMechanisms
import domain.objects.HHCellObject
import domain.objects.HHSignals
import domain.objects.SynapseObject
import org.jetbrains.research.mads.core.configuration.configure
import org.jetbrains.research.mads.core.configuration.pathway
import org.jetbrains.research.mads.core.simulation.Model
import org.jetbrains.research.mads.core.telemetry.FileSaver
import org.jetbrains.research.mads.core.types.ModelObject
import org.jetbrains.research.mads.core.types.responses.DynamicResponse
import kotlin.random.Random
import kotlin.system.measureTimeMillis

fun main() {
//    createHHCellsExperiment()
//    createSynapseExperiment()
    createTwoPopulationsExperiment()
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

fun createSynapseExperiment() {
    val I_exp = 8.0

    val cellSource = HHCellObject(HHSignals(I_e = I_exp, V = 0.0, N = 0.32, M = 0.05, H = 0.6))
    val cellDest = HHCellObject(HHSignals(I_e = I_exp, V = -65.0, N = 0.32, M = 0.05, H = 0.6))

    val synapse = SynapseObject(cellSource, cellDest)

    val config = configure {
        addPathway(hhPathway())
        addPathway(pathway<SynapseObject> {
            mechanism(mechanism = SynapseMechanisms.SpikeTransfer, SynapseParamsNoSave) {
                duration = 2
            }
            mechanism(mechanism = SynapseMechanisms.SynapseDecay, SynapseParamsNoSave) {
                duration = 10
            }
        })
    }
    val s = Model(arrayListOf(cellSource, cellDest, synapse), config)

    val elapsed = measureTimeMillis {
        s.simulate { it.currentTime() > 100_000 }
    }
    println("Time taken: $elapsed")
    println("Already calculated")
}

fun createTwoPopulationsExperiment() {
    FileSaver.initModelWriters("log/${System.currentTimeMillis()}/", setOf(DynamicResponse::class))

    val I_exp = 8.0
    val excCount = 80
    val inhCount = 20

    val excCells: ArrayList<HHCellObject> = arrayListOf()

    for (i in 0 until excCount) {
        excCells.add(
            HHCellObject(
                HHSignals(I_e = I_exp, V = Random.nextDouble(-65.0, 0.0), N = 0.32, M = 0.05, H = 0.6),
                constantCurrent = false
            )
        )
    }

    val inhibCells: ArrayList<HHCellObject> = arrayListOf()

    for (i in 0 until inhCount) {
        inhibCells.add(
            HHCellObject(
                HHSignals(I_e = 5.0, V = Random.nextDouble(-65.0, 0.0), N = 0.32, M = 0.05, H = 0.6),
                constantCurrent = false
            )
        )
    }

    val synapses: ArrayList<SynapseObject> = arrayListOf()

    for (i in 0 until excCount) {
        for (j in 0 until excCount) {
            if (i != j) {
                val synapse = SynapseObject(excCells[i], excCells[j])
                synapses.add(synapse)
            }
        }
    }

    for (i in 0 until excCount) {
        for (j in 0 until inhCount) {
            val synapse = SynapseObject(excCells[i], inhibCells[j])
            synapses.add(synapse)
        }
    }

    for (i in 0 until inhCount) {
        for (j in 0 until inhCount) {
            if (i != j) {
                val synapse = SynapseObject(inhibCells[i], inhibCells[j], isInhibitory = true)
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
        addPathway(pathway<SynapseObject> {
            mechanism(mechanism = SynapseMechanisms.SpikeTransfer, SynapseParamsNoSave) {
                duration = 2
            }
//            mechanism(mechanism = SynapseMechanisms.SynapseDecay, SynapseParamsNoSave) {
//                duration = 10
//            }
        })
    }

    val s = Model(allObjects, config)

    val elapsed = measureTimeMillis {
        s.simulate { it.currentTime() > 25_000 }
    }
    FileSaver.closeModelWriters()
    println("Time taken: $elapsed")
    println("Already calculated")

//    for (i in 0 until excCount) {
//        val fname = "mads_data//exc_${i}th_neuron.txt"
//        File(fname).bufferedWriter().use { out ->
//            out.write("I_ext;I;V;N;M;H\n")
//
//            var cellHist = excCells[i].history[HHSignals::class]
//
//            cellHist?.forEach {
//                it as HHSignals
//                out.write("${it.I_ext};${it.I};${it.V}; ${it.N};${it.M};${it.H}\n")
//            }
//        }
//    }
//
//    for (i in 0 until inhCount) {
//        val fname = "mads_data//inh_${i}th_neuron.txt"
//        File(fname).bufferedWriter().use { out ->
//            out.write("I_ext;I;V;N;M;H\n")
//
//            var cellHist = inhibCells[i].history[HHSignals::class]
//
//            cellHist?.forEach {
//                it as HHSignals
//                out.write("${it.I_ext};${it.I};${it.V}; ${it.N};${it.M};${it.H}\n")
//            }
//        }
//    }

}