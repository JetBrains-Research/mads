package org.jetbrains.research.experiments

import org.jetbrains.research.mads.core.configuration.configure
import org.jetbrains.research.mads.core.simulation.Model
import org.jetbrains.research.mads.core.telemetry.FileSaver
import org.jetbrains.research.mads.core.types.ModelObject
import org.jetbrains.research.mads_ns.electrode.Electrode
import org.jetbrains.research.mads_ns.pathways.*
import org.jetbrains.research.mads_ns.physiology.neurons.CurrentSignals
import org.jetbrains.research.mads_ns.physiology.neurons.izh.IzhConstantsRS
import org.jetbrains.research.mads_ns.physiology.neurons.izh.IzhNeuron
import org.jetbrains.research.mads_ns.physiology.neurons.izh.IzhSignals
import org.jetbrains.research.mads_ns.synapses.SynapseSignals
import kotlin.random.Random

fun main() {
    IzhikevichCellsExperiment()
//    IzhikevichTwoCellsExperiment()
}

fun IzhikevichCellsExperiment() {
    FileSaver.initModelWriters(
        "log/izh_one/${System.currentTimeMillis()}/"
    )
    val rnd: Random = Random(12345L)

    val objects: ArrayList<ModelObject> = arrayListOf()
    val neuronCount = 1
    for (i in 0 until neuronCount) {
        val cell = IzhNeuron(IzhConstantsRS.V_thresh, IzhSignals())
        val electrode = Electrode(CurrentSignals(I_e = 20.0), rnd)
        electrode.connectToCell(cell)
        objects.add(cell)
        objects.add(electrode)
    }

    val config = configure {
        addPathway(electrodePathway())
        addPathway(izhPathway())
    }

    val s = Model(objects, config)
    s.simulate { it.currentTime() > 100_000 }
    FileSaver.closeModelWriters()
}

fun IzhikevichTwoCellsExperiment() {
//    FileSaver.initModelWriters("log/${System.currentTimeMillis()}/", setOf(SignalDoubleChangeResponse::class))
    FileSaver.initModelWriters(
        "log/izh_two/${System.currentTimeMillis()}/"
    )
    val rnd: Random = Random(12345L)

    val electrode = Electrode(CurrentSignals(I_e = 20.0), rnd)
    val fNeuron = IzhNeuron(IzhConstantsRS.V_thresh, IzhSignals())
    val sNeuron = IzhNeuron(IzhConstantsRS.V_thresh, IzhSignals())

    electrode.connectToCell(fNeuron)
    val synapse = connectCellsWithSynapse(fNeuron, sNeuron, false, CurrentSignals(0.0), SynapseSignals())

    val objects: ArrayList<ModelObject> = arrayListOf(electrode, fNeuron, sNeuron, synapse)

    val config = configure {
        addPathway(electrodePathway())
        addPathway(izhPathway())
        addPathway(synapsePathway())
    }

    val s = Model(objects, config)
    s.simulate { it.currentTime() > 100_000 }
    FileSaver.closeModelWriters()
}