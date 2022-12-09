package org.jetbrains.research.experiments.examples
//
//import org.jetbrains.research.mads.core.configuration.configure
//import org.jetbrains.research.mads.core.simulation.Model
//import org.jetbrains.research.mads.core.telemetry.FileSaver
//import org.jetbrains.research.mads.core.types.ModelObject
//import org.jetbrains.research.mads_ns.data_provider.MnistProvider
//import org.jetbrains.research.mads_ns.electrode.Electrode
//import org.jetbrains.research.mads_ns.electrode.ElectrodeArray
//import org.jetbrains.research.mads_ns.pathways.*
//import org.jetbrains.research.mads_ns.physiology.neurons.CurrentSignals
//import org.jetbrains.research.mads_ns.physiology.neurons.Neuron
//import org.jetbrains.research.mads_ns.physiology.neurons.hh.HHConstants
//import org.jetbrains.research.mads_ns.physiology.neurons.hh.HHSignals
//import org.jetbrains.research.mads_ns.synapses.Synapse
//import org.jetbrains.research.mads_ns.synapses.SynapseSignals
//import java.io.File
//import kotlin.random.Random
//import kotlin.system.measureTimeMillis
//
//fun main() {
////    createHHCellsExperiment()
//    createSynapseExperiment()
////    createTwoPopulationsExperiment()
////    createTrainingExperiment()
////    createTrainingExperimentConvolve()
////    createTrainingExperimentExcInhib()
////    createSimpleTriplet()
//}
//
//fun createSimpleTriplet()
//{
//    val logPath = "log/latest/"
////    FileSaver.initModelWriters(logPath, setOf(SignalDoubleChangeResponse::class))
//
//    val objects: ArrayList<ModelObject> = arrayListOf()
//
//    val firstLayer: ArrayList<Neuron> = arrayListOf()
//    val secondLayer: ArrayList<Neuron> = arrayListOf()
//
//    for(i in 0 until 2) {
//        val cell = Neuron(HHConstants.V_thresh, HHSignals())
//        firstLayer.add(cell)
//    }
//
//    for(i in 0 until 2) {
//        val secondCell = Neuron(HHConstants.V_thresh, HHSignals())
//        secondLayer.add(secondCell)
//    }
//
//    val synapses: ArrayList<Synapse> = arrayListOf()
//
//    for(i in 0 until firstLayer.size) {
//        for(j in 0 until secondLayer.size) {
//            if(i == 0 && j == 0) {
//                continue
//            }
//            val syn = connectCellsWithSynapse(firstLayer[i], secondLayer[j], false, CurrentSignals(0.0), SynapseSignals(weight = 10.0))
//            synapses.add(syn)
//
//            println(syn.hashCode())
//        }
//    }
//
//    println(synapses.size)
//
//    objects.addAll(firstLayer)
//    objects.addAll(secondLayer)
//    objects.addAll(synapses)
//
//    val config = configure {
////        addPathway(electrodePathway())
//        addPathway(synapsePathway())
//        addPathway(electrodeArrayPathway())
//        addPathway(hhPathway())
//    }
//
//    val s = Model(objects, config)
//    s.simulate { it.currentTime() > 100_000 }
//    FileSaver.closeModelWriters()
//
//    File("${logPath}/second_layer.txt").printWriter().use { out ->
//        secondLayer.forEach {
//            out.println("${it.hashCode()}")
//        }
//    }
//
//    synapses.forEach {
//        val w = (it.signals[SynapseSignals::class] as SynapseSignals).weight
////            out.println("${w}")
//        println(w)
//    }
//
//
//}
//
//fun _connect_receptive_field(prevLayer: ArrayList<Neuron>, cell : Neuron, x: Int, y: Int, width: Int, height: Int, size: Int): ArrayList<Synapse> {
//    val res: ArrayList<Synapse> = arrayListOf()
//
//    val offset = size / 2
//    val centerIdx = y*width + x
//
//    for(i in -offset .. offset) {
//        for(j in -offset .. offset) {
//            val flatIdx = centerIdx + j + i*width
//            val srcCell = prevLayer[flatIdx]
//
//            val syn = connectCellsWithSynapse(srcCell, cell, false, CurrentSignals(0.0), SynapseSignals(weight=1.0))
//            res.add(syn)
//        }
//    }
//
//    return res
//}
//
//fun createTrainingExperimentConvolve() {
////    val logPath = "log/${System.currentTimeMillis()}/"
//    val logPath = "log/allClasses/"
////    FileSaver.initModelWriters(logPath, setOf(SignalDoubleChangeResponse::class, SignalBooleanChangeResponse::class))
////    FileSaver.initModelWriters(logPath, setOf(SignalBooleanChangeResponse::class))
//
//    val targetClasses = arrayListOf<String>("0", "1", "2", "3", "4", "5", "6", "7", "8", "9")
//    val provider = MnistProvider("C:\\projects\\mads\\mads_data\\MNIST_training\\", targetClasses)
//    val electrodesArray = ElectrodeArray(provider, 25.0)
//
//    val objects: ArrayList<ModelObject> = arrayListOf()
//
//    objects.add(electrodesArray)
//
//    val firstLayer: ArrayList<Neuron> = arrayListOf()
//    val secondLayer: ArrayList<Neuron> = arrayListOf()
//    val thirdLayer: ArrayList<Neuron> = arrayListOf()
//
//    val synapses: ArrayList<Synapse> = arrayListOf()
//    val synapsesSecondToThird: ArrayList<Synapse> = arrayListOf()
//
//    for(i in 0 until provider.width) {
//        for(j in 0 until provider.height) {
//            val cell = Neuron(HHConstants.V_thresh, HHSignals())
//            val electrode = electrodesArray.getElectrodeByCoordinate(i, j)
//            electrode.connectToCell(cell)
//
//            firstLayer.add(cell)
//        }
//    }
//
////    for(i in 0 until 100) {
////        val secondCell = HHCell(CurrentSignals(I_e = 5.0), HHSignals(V = -65.0, N = 0.32, M = 0.05, H = 0.6))
////        secondLayer.add(secondCell)
////    }
//
//    for(i in 1 until provider.width-1 step 2) {
//        for(j in 1 until provider.height-1 step 2) {
//            val secondCell = Neuron(HHConstants.V_thresh, HHSignals())
//            val connections = _connect_receptive_field(firstLayer, secondCell, i, j, provider.width, provider.height, 3)
//
//            synapses.addAll(connections)
//            secondLayer.add(secondCell)
//        }
//    }
//
//    for(i in 0 until 25) {
//        val thirdCell = Neuron(HHConstants.V_thresh, HHSignals())
//        thirdLayer.add(thirdCell)
//    }
//
//    for(i in 0 until secondLayer.size) {
//        for(j in 0 until thirdLayer.size) {
//            val weight = 1.0 + Random.nextDouble() - 0.25
//            val syn = connectCellsWithSynapse(secondLayer[i], thirdLayer[j], false, CurrentSignals(0.0), SynapseSignals(weight=weight))
//            synapsesSecondToThird.add(syn)
//        }
//    }
//
//    objects.addAll(firstLayer)
//    objects.addAll(secondLayer)
//    objects.addAll(thirdLayer)
//    objects.addAll(synapses)
//    objects.addAll(synapsesSecondToThird)
//
//    println(secondLayer.size)
//
//    val config = configure {
////        addPathway(electrodePathway())
//        addPathway(synapsePathway())
//        addPathway(electrodeArrayPathway())
//        addPathway(hhPathway())
//    }
//
//    val s = Model(objects, config)
//    s.simulate { it.currentTime() > 500_000 }
//    FileSaver.closeModelWriters()
//
//    File("${logPath}/second_layer.txt").printWriter().use { out ->
//        secondLayer.forEach {
//            out.println("${it.hashCode()}")
//        }
//    }
//
//    File("${logPath}/synapse1to2_weights.txt").printWriter().use { out ->
//        synapses.forEach {
//            val w = (it.signals[SynapseSignals::class] as SynapseSignals).weight
//            out.println("${w}")
//        }
//    }
//
//    File("${logPath}/synapse2to3_weights.txt").printWriter().use { out ->
//        synapsesSecondToThird.forEach {
//            val w = (it.signals[SynapseSignals::class] as SynapseSignals).weight
//            out.println("${w}")
//        }
//    }
//}
//
//fun createTrainingExperimentExcInhib() {
////    val logPath = "log/${System.currentTimeMillis()}/"
//    val logPath = "log/excInhib/"
////    FileSaver.initModelWriters(logPath, setOf(SignalDoubleChangeResponse::class, SignalBooleanChangeResponse::class))
////    FileSaver.initModelWriters(logPath, setOf(SignalBooleanChangeResponse::class))
//
//    val n_exc = 100
//    val n_inhib = n_exc
//
//    val targetClasses = arrayListOf<String>("0", "1", "2")
//    val provider = MnistProvider("C:\\projects\\mads\\mads_data\\MNIST_training\\", targetClasses)
//    val electrodesArray = ElectrodeArray(provider, 25.0)
//
//    val objects: ArrayList<ModelObject> = arrayListOf()
//
//    objects.add(electrodesArray)
//
//    val firstLayer: ArrayList<Neuron> = arrayListOf()
//    val secondLayer: ArrayList<Neuron> = arrayListOf()
//    val thirdLayer: ArrayList<Neuron> = arrayListOf()
//
//    val synapses: ArrayList<Synapse> = arrayListOf()
//    val synapsesSecondToThird: ArrayList<Synapse> = arrayListOf()
//    val synapsesThirdToSecond: ArrayList<Synapse> = arrayListOf()
//
//    for(i in 0 until provider.width) {
//        for(j in 0 until provider.height) {
//            val cell = Neuron(HHConstants.V_thresh, HHSignals())
//            val electrode = electrodesArray.getElectrodeByCoordinate(i, j)
//            electrode.connectToCell(cell)
//
//            firstLayer.add(cell)
//        }
//    }
//
//    for(i in 0 until n_inhib) {
//        val cell = Neuron(HHConstants.V_thresh, HHSignals())
//
//        secondLayer.add(cell)
//    }
//
//    for(i in 0 until n_exc) {
//        val cell = Neuron(HHConstants.V_thresh, HHSignals())
//
//        thirdLayer.add(cell)
//    }
//
//    for(i in 0 until firstLayer.size) {
//        for(j in 0 until secondLayer.size) {
//            val weight = 1.0 + Random.nextDouble() - 0.25
//            val syn = connectCellsWithSynapse(firstLayer[i], secondLayer[j], false, CurrentSignals(0.0), SynapseSignals(weight=weight))
//            synapses.add(syn)
//        }
//    }
//
//    for(i in 0 until secondLayer.size) {
//        val weight = 1.0 + Random.nextDouble() - 0.25
//        val syn = connectCellsWithSynapse(secondLayer[i], thirdLayer[i], false, CurrentSignals(0.0), SynapseSignals(weight=weight))
//        synapsesSecondToThird.add(syn)
//    }
//
//    for(i in 0 until secondLayer.size) {
//        for(j in 0 until thirdLayer.size) {
//            if(i == j)
//            {
//                continue
//            }
//
//            val weight = 1.0 + Random.nextDouble() - 0.25
//            val syn = connectCellsWithSynapse(thirdLayer[j], secondLayer[i], true, CurrentSignals(0.0), SynapseSignals(weight=weight))
//            synapsesThirdToSecond.add(syn)
//        }
//    }
//
//
//    objects.addAll(firstLayer)
//    objects.addAll(secondLayer)
//    objects.addAll(thirdLayer)
//    objects.addAll(synapses)
//    objects.addAll(synapsesSecondToThird)
//
//    println(secondLayer.size)
//
//    val config = configure {
//        addPathway(synapsePathway())
//        addPathway(electrodeArrayPathway())
//        addPathway(hhPathway())
//    }
//
//    val s = Model(objects, config)
//    s.simulate { it.currentTime() > 100_000 }
//    FileSaver.closeModelWriters()
//
//    File("${logPath}/second_layer.txt").printWriter().use { out ->
//        secondLayer.forEach {
//            out.println("${it.hashCode()}")
//        }
//    }
//
//    File("${logPath}/synapse1to2_weights.txt").printWriter().use { out ->
//        synapses.forEach {
//            val w = (it.signals[SynapseSignals::class] as SynapseSignals).weight
//            out.println("${w}")
//        }
//    }
//
//    File("${logPath}/synapse2to3_weights.txt").printWriter().use { out ->
//        synapsesSecondToThird.forEach {
//            val w = (it.signals[SynapseSignals::class] as SynapseSignals).weight
//            out.println("${w}")
//        }
//    }
//
//    File("${logPath}/synapse3to2_weights.txt").printWriter().use { out ->
//        synapsesThirdToSecond.forEach {
//            val w = (it.signals[SynapseSignals::class] as SynapseSignals).weight
//            out.println("${w}")
//        }
//    }
//}
//
//fun createTrainingExperiment() {
////    val logPath = "log/${System.currentTimeMillis()}/"
//    val logPath = "log/latest/"
////    FileSaver.initModelWriters(logPath, setOf(SignalDoubleChangeResponse::class))
//
//    val targetClasses = arrayListOf<String>("0", "1")
//    val provider = MnistProvider("C:\\projects\\mads\\mads_data\\MNIST_training\\", targetClasses)
//    val electrodesArray = ElectrodeArray(provider, 25.0)
//
//    val objects: ArrayList<ModelObject> = arrayListOf()
//
//    objects.add(electrodesArray)
//
//    val firstLayer: ArrayList<Neuron> = arrayListOf()
//    val secondLayer: ArrayList<Neuron> = arrayListOf()
//    val thirdLayer: ArrayList<Neuron> = arrayListOf()
//
//    for(i in 0 until provider.width) {
//        for(j in 0 until provider.height) {
//            val cell = Neuron(HHConstants.V_thresh, HHSignals())
//            val electrode = electrodesArray.getElectrodeByCoordinate(i, j)
//            electrode.connectToCell(cell)
//
//            firstLayer.add(cell)
//        }
//    }
//
//    for(i in 0 until 100) {
//        val secondCell = Neuron(HHConstants.V_thresh, HHSignals())
//        secondLayer.add(secondCell)
//    }
//
//    for(i in 0 until 25) {
//        val thirdCell = Neuron(HHConstants.V_thresh, HHSignals())
//        thirdLayer.add(thirdCell)
//    }
//
//    val synapses: ArrayList<Synapse> = arrayListOf()
//    val synapsesSecondToThird: ArrayList<Synapse> = arrayListOf()
//
//    for(i in 0 until firstLayer.size) {
//        for(j in 0 until secondLayer.size) {
//            val weight = 1.0 + Random.nextDouble() - 0.25
//            val syn = connectCellsWithSynapse(firstLayer[i], secondLayer[j], false, CurrentSignals(0.0), SynapseSignals(weight=weight))
//            synapses.add(syn)
//        }
//    }
//
//    for(i in 0 until secondLayer.size) {
//        for(j in 0 until thirdLayer.size) {
//            val weight = 1.0 + Random.nextDouble() - 0.25
//            val syn = connectCellsWithSynapse(secondLayer[i], thirdLayer[j], false, CurrentSignals(0.0), SynapseSignals(weight=weight))
//            synapsesSecondToThird.add(syn)
//        }
//    }
//
//    objects.addAll(firstLayer)
//    objects.addAll(secondLayer)
//    objects.addAll(thirdLayer)
//    objects.addAll(synapses)
//    objects.addAll(synapsesSecondToThird)
//
//    val config = configure {
////        addPathway(electrodePathway())
//        addPathway(synapsePathway())
//        addPathway(electrodeArrayPathway())
//        addPathway(hhPathway())
//    }
//
//    val s = Model(objects, config)
//    s.simulate { it.currentTime() > 50_000 }
//    FileSaver.closeModelWriters()
//
//    File("${logPath}/second_layer.txt").printWriter().use { out ->
//        secondLayer.forEach {
//            out.println("${it.hashCode()}")
//        }
//    }
//
//    File("${logPath}/synapse1to2_weights.txt").printWriter().use { out ->
//        synapses.forEach {
//            val w = (it.signals[SynapseSignals::class] as SynapseSignals).weight
//            out.println("${w}")
//        }
//    }
//
//    File("${logPath}/synapse2to3_weights.txt").printWriter().use { out ->
//        synapsesSecondToThird.forEach {
//            val w = (it.signals[SynapseSignals::class] as SynapseSignals).weight
//            out.println("${w}")
//        }
//    }
//}
//
//fun createHHCellsExperiment() {
////    FileSaver.initModelWriters("log/${System.currentTimeMillis()}/", setOf(SignalDoubleChangeResponse::class))
//    val I_exp = 8.0
//    val rnd: Random = Random(12345L)
//
//    val objects: ArrayList<ModelObject> = arrayListOf()
//    val neuronCount = 1
//    for (i in 0 until neuronCount) {
//        val cell = Neuron(HHConstants.V_thresh, HHSignals())
//        val electrode = Electrode(CurrentSignals(I_e = I_exp), rnd)
//        electrode.connectToCell(cell)
//        objects.add(cell)
//        objects.add(electrode)
//    }
//
//    val config = configure {
////        addPathway(electrodePathway())
//        addPathway(electrodeArrayPathway())
//        addPathway(hhPathway())
//    }
//
//    val s = Model(objects, config)
//    s.simulate { it.currentTime() > 100_000 }
//    FileSaver.closeModelWriters()
//}
//
//fun createSynapseExperiment() {
////    FileSaver.initModelWriters("log/${System.currentTimeMillis()}/", setOf(SignalDoubleChangeResponse::class, SignalBooleanChangeResponse::class))
//    val I_exp = 8.0
//    val rnd: Random = Random(12345L)
//
//    val electrode = Electrode(CurrentSignals(I_e = I_exp), rnd)
//    val cellSource = Neuron(HHConstants.V_thresh, HHSignals())
//    val cellDest = Neuron(HHConstants.V_thresh, HHSignals())
//
//    electrode.connectToCell(cellSource)
//    val synapse = connectCellsWithSynapse(cellSource, cellDest, false, CurrentSignals(0.0), SynapseSignals())
//
//    val config = configure {
//        addPathway(electrodePathway())
//        addPathway(neuronPathway())
//        addPathway(hhPathway())
//        addPathway(synapsePathway())
//    }
//    val s = Model(arrayListOf(electrode, cellSource, cellDest, synapse), config)
//
//    val elapsed = measureTimeMillis {
//        s.simulate { it.currentTime() > 100_000 }
//    }
//
//    FileSaver.closeModelWriters()
//    println("Time taken: $elapsed")
//    println("Already calculated")
//}
//
////fun createTwoPopulationsExperiment() {
////    FileSaver.initModelWriters("log/${System.currentTimeMillis()}/", setOf(SignalDoubleChangeResponse::class, SignalBooleanChangeResponse::class))
////
////    val I_exp = 8.0
////    val excCount = 80
////    val inhCount = 20
////
////    val excCells: ArrayList<HHNeuron> = arrayListOf()
////    // TODO: @vlad0922 we need to connect electrodes to some (all?) excititory neurons and deal with pulse activity
////
////    for (i in 0 until excCount) {
////        excCells.add(
////            HHNeuron(
////                CurrentSignals(I_e = I_exp),
////                HHSignals(V = Random.nextDouble(-65.0, 0.0), N = 0.32, M = 0.05, H = 0.6),
////                constantCurrent = false
////            )
////        )
////    }
////
////    val inhibCells: ArrayList<HHNeuron> = arrayListOf()
////
////    for (i in 0 until inhCount) {
////        inhibCells.add(
////            HHNeuron(
////                CurrentSignals(I_e = 5.0),
////                HHSignals(V = Random.nextDouble(-65.0, 0.0), N = 0.32, M = 0.05, H = 0.6),
////                constantCurrent = false
////            )
////        )
////    }
////
////    val synapses: ArrayList<Synapse> = arrayListOf()
////
////    for (i in 0 until excCount) {
////        for (j in 0 until excCount) {
////            if (i != j) {
////                val synapse = connectCellsWithSynapse(excCells[i], excCells[j],false, CurrentSignals(0.0), SynapseSignals())
////                synapses.add(synapse)
////            }
////        }
////    }
////
////    for (i in 0 until excCount) {
////        for (j in 0 until inhCount) {
////            val synapse = connectCellsWithSynapse(excCells[i], inhibCells[j],false, CurrentSignals(0.0), SynapseSignals())
////            synapses.add(synapse)
////        }
////    }
////
////    for (i in 0 until inhCount) {
////        for (j in 0 until inhCount) {
////            if (i != j) {
////                val synapse = connectCellsWithSynapse(inhibCells[i], inhibCells[j], inhibitory = true, CurrentSignals(0.0), SynapseSignals())
////                synapses.add(synapse)
////            }
////        }
////    }
////
////    val allObjects: ArrayList<ModelObject> = arrayListOf()
////    allObjects.addAll(inhibCells)
////    allObjects.addAll(excCells)
////    allObjects.addAll(synapses)
////
////    val config = configure {
////        addPathway(hhPathway())
////        addPathway(synapsePathway())
////    }
////
////    val s = Model(allObjects, config)
////
////    val elapsed = measureTimeMillis {
////        s.simulate { it.currentTime() > 25_000 }
////    }
////    FileSaver.closeModelWriters()
////    println("Time taken: $elapsed")
////    println("Already calculated")
////}