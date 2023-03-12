package org.jetbrains.research.experiments.examples

//fun main() {
////    IzhikevichCellsExperiment()
//    IzhikevichTwoCellsExperiment()
//}
//
//fun IzhikevichCellsExperiment() {
//    FileSaver.initModelWriters(
//        "log/izh_one/${System.currentTimeMillis()}/"
//    )
//    val rnd = Random(12345L)
//
//    val objects: ArrayList<ModelObject> = arrayListOf()
//    val neuronCount = 1
//    for (i in 0 until neuronCount) {
//        val cell = IzhNeuron(IzhConstantsRS.V_thresh, IzhSignals())
//        cell.type = "neuron"
//        val electrode = Electrode(CurrentSignals(I_e = 20.0), rnd)
//        electrode.type = "electrode"
//        electrode.connectToCell(cell)
//        objects.add(cell)
//        objects.add(electrode)
//    }
//
//    val config = configure {
//        addPathway(electrodePathway())
//        addPathway(izhPathway())
//    }
//
//    val s = Model(objects, config)
//    s?.simulate { it.currentTime() > 100_000 }
//    FileSaver.closeModelWriters()
//}
//
//fun IzhikevichTwoCellsExperiment() {
////    FileSaver.initModelWriters("log/${System.currentTimeMillis()}/", setOf(SignalDoubleChangeResponse::class))
//    FileSaver.initModelWriters(
//        "log/izh_two/${System.currentTimeMillis()}/"
//    )
//    val rnd: Random = Random(12345L)
//
//    val electrode = Electrode(CurrentSignals(I_e = 20.0), rnd)
//    val fNeuron = IzhNeuron(IzhConstantsRS.V_thresh, IzhSignals())
//    fNeuron.type = "input"
//    val sNeuron = IzhNeuron(IzhConstantsRS.V_thresh, IzhSignals())
//    sNeuron.type = "output"
//
//    electrode.connectToCell(fNeuron)
//    val synapse = connectCellsWithSynapse(fNeuron, sNeuron, false, CurrentSignals(0.0), SynapseSignals())
//
//    val objects: ArrayList<ModelObject> = arrayListOf(electrode, fNeuron, sNeuron, synapse)
//
//    val config = configure {
//        addPathway(electrodePathway())
//        addPathway(izhPathway())
//        addPathway(synapsePathway())
//    }
//
//    val s = Model(objects, config)
//    s?.simulate { it.currentTime() > 1_000_000 }
//    FileSaver.closeModelWriters()
//}