package domain

import domain.mechanisms.*
import domain.objects.DynamicObject
import domain.objects.HHCellObject
import domain.objects.HHSignals
import domain.objects.SynapseObject
import org.jetbrains.research.mads.core.configuration.Configuration
import org.jetbrains.research.mads.core.configuration.Pathway
import org.jetbrains.research.mads.core.simulation.Model
import org.jetbrains.research.mads.core.types.ModelObject
import java.io.File
import kotlin.system.measureTimeMillis

fun main() {
//    createHHCellsExperiment()
//    createDynamicExperimentMultipleI()
//    createHHHundredCellsExperiment()
//    createSynapseExperiment()
    createTwoPopulationsExperiment()
}



fun createHHCellsExperiment()
{
    val I_exp = 8.0
    val dynamic = HHCellObject(HHSignals(I = I_exp, V = -65.0, N = 0.32, M = 0.05, H= 0.6))

    val config = Configuration()

    val pathwayDynamic: Pathway<HHCellObject> = Pathway()
    pathwayDynamic.add(HHCellObject::IDynamicMechanism, SimpleParameters(1.0), 2) { true }
    pathwayDynamic.add(HHCellObject::VDynamicMechanism, SimpleParameters(1.0), 2) { true }
    pathwayDynamic.add(HHCellObject::NDynamicMechanism, SimpleParameters(1.0), 2) { true }
    pathwayDynamic.add(HHCellObject::MDynamicMechanism, SimpleParameters(1.0), 2) { true }
    pathwayDynamic.add(HHCellObject::HDynamicMechanism, SimpleParameters(1.0), 2) { true }

    config.add(HHCellObject::class, arrayListOf(pathwayDynamic))

    val s = Model(arrayListOf(dynamic), config)

    s.simulate { it.currentTime() > 100000 }

//    var fname = "i_${I_exp}.txt"
//
//    File(fname).bufferedWriter().use { out ->
//        out.write("I;V;N;M;H\n")
//        dynamic.history.forEach {
//            it as HHSignals
//            out.write("${it.I};${it.V}; ${it.N};${it.M};${it.H}\n")
//        }
//    }
}

fun createDynamicExperimentMultipleI() {
    for(iv in 2 .. 30 step 1)
    {
        println(iv)
        val I_exp = iv / 2.0
        val dynamic = HHCellObject(HHSignals(I = I_exp, V = -65.0, N = 0.32, M = 0.05, H= 0.6))

        val config = Configuration()

        val pathwayDynamic: Pathway<HHCellObject> = Pathway()
        pathwayDynamic.add(HHCellObject::IDynamicMechanism, SimpleParameters(1.0), 2) { true }
        pathwayDynamic.add(HHCellObject::VDynamicMechanism, SimpleParameters(1.0), 2) { true }
        pathwayDynamic.add(HHCellObject::NDynamicMechanism, SimpleParameters(1.0), 2) { true }
        pathwayDynamic.add(HHCellObject::MDynamicMechanism, SimpleParameters(1.0), 2) { true }
        pathwayDynamic.add(HHCellObject::HDynamicMechanism, SimpleParameters(1.0), 2) { true }


        config.add(HHCellObject::class, arrayListOf(pathwayDynamic))

        val s = Model(arrayListOf(dynamic), config)
        s.simulate { it.currentTime() > 100000 }
//
//        println((dynamic.signals as HHSignals))
//        println((dynamic.history.size))

//        var fname = "i_${I_exp}.txt"
//
//        File(fname).bufferedWriter().use { out ->
//            out.write("I;V;N;M;H\n")
//            dynamic.history.forEach {
//                it as HHSignals
//                out.write("${it.I};${it.V}; ${it.N};${it.M};${it.H}\n")
//            }
//        }
    }
}

fun createHHHundredCellsExperiment()
{
    val I_exp = 8.0

    val cells : ArrayList<HHCellObject> = arrayListOf()
    val neuronCount = 10000
    for (i in 0 until neuronCount) {
        cells.add(HHCellObject(HHSignals(I = I_exp, V = -65.0, N = 0.32, M = 0.05, H= 0.6)))
    }

    val config = Configuration()

    val pathwayDynamic: Pathway<HHCellObject> = Pathway()
    pathwayDynamic.add(HHCellObject::IDynamicMechanism, SimpleParameters(1.0), 2) { true }
    pathwayDynamic.add(HHCellObject::VDynamicMechanism, SimpleParameters(1.0), 2) { true }
    pathwayDynamic.add(HHCellObject::NDynamicMechanism, SimpleParameters(1.0), 2) { true }
    pathwayDynamic.add(HHCellObject::MDynamicMechanism, SimpleParameters(1.0), 2) { true }
    pathwayDynamic.add(HHCellObject::HDynamicMechanism, SimpleParameters(1.0), 2) { true }

    config.add(HHCellObject::class, arrayListOf(pathwayDynamic))

    val s = Model(cells, config)

    val elapsed = measureTimeMillis {
        s.simulate { it.currentTime() > 100_000 }
    }
    println("Time taken: $elapsed")
    println("Already calculated")

//    for (i in 0 until neuronCount) {
//        val fname = "${i}th_neuron.txt"
//        File(fname).bufferedWriter().use { out ->
//            out.write("I;V;N;M;H\n")
//            cells[i].history.forEach {
//                it as HHSignals
//                out.write("${it.I};${it.V}; ${it.N};${it.M};${it.H}\n")
//            }
//        }
//    }
}

fun createSynapseExperiment()
{
    val I_exp = 8.0

    var cellSource = HHCellObject(HHSignals(I = I_exp, V = 0.0, N = 0.32, M = 0.05, H= 0.6))
    var cellDest = HHCellObject(HHSignals(I = I_exp, V = -65.0, N = 0.32, M = 0.05, H= 0.6))

    var synapse = SynapseObject(cellSource, cellDest)

    val config = Configuration()

    val pathwayDynamic: Pathway<HHCellObject> = Pathway()
    pathwayDynamic.add(HHCellObject::IDynamicMechanism, SimpleParameters(1.0), 2) { true }
    pathwayDynamic.add(HHCellObject::VDynamicMechanism, SimpleParameters(1.0), 2) { true }
    pathwayDynamic.add(HHCellObject::NDynamicMechanism, SimpleParameters(1.0), 2) { true }
    pathwayDynamic.add(HHCellObject::MDynamicMechanism, SimpleParameters(1.0), 2) { true }
    pathwayDynamic.add(HHCellObject::HDynamicMechanism, SimpleParameters(1.0), 2) { true }

    val pathwaySynapse: Pathway<SynapseObject> = Pathway()
    pathwaySynapse.add(SynapseObject::spikeTransferMechanism, SimpleParameters(1.0), 2) {true}
    pathwaySynapse.add(SynapseObject::synapseDecayMechanism, SimpleParameters(1.0), 10) {true}

    config.add(SynapseObject::class, arrayListOf(pathwaySynapse))
    config.add(HHCellObject::class, arrayListOf(pathwayDynamic))


    val s = Model(arrayListOf(cellSource, cellDest, synapse), config)

    val elapsed = measureTimeMillis {
        s.simulate { it.currentTime() > 100_000 }
    }
    println("Time taken: $elapsed")
    println("Already calculated")

    println(synapse.weight)

//    var fname = "source_neuron.txt"
//    File(fname).bufferedWriter().use { out ->
//        var cellHist = cellSource.history[HHSignals::class]
//
//        out.write("I;V;N;M;H\n")
//        cellHist?.forEach {
//            it as HHSignals
//            out.write("${it.I};${it.V}; ${it.N};${it.M};${it.H}\n")
//        }
//    }
//
//    fname = "dest_neuron.txt"
//    File(fname).bufferedWriter().use { out ->
//        var cellHist = cellDest.history[HHSignals::class]
//
//        out.write("I;V;N;M;H\n")
//        cellHist?.forEach {
//            it as HHSignals
//            out.write("${it.I};${it.V}; ${it.N};${it.M};${it.H}\n")
//        }
//    }

}

fun createTwoPopulationsExperiment()
{
    val I_exp = 8.0
    val excCount = 80
    val inhCount = 20

    val excCells : ArrayList<HHCellObject> = arrayListOf()

    for (i in 0 until excCount) {
        excCells.add(HHCellObject(HHSignals(I = I_exp, V = -65.0, N = 0.32, M = 0.05, H= 0.6)))
    }

    val inhibCells : ArrayList<HHCellObject> = arrayListOf()

    for (i in 0 until inhCount) {
        inhibCells.add(HHCellObject(HHSignals(I = 5.0, V = -65.0, N = 0.32, M = 0.05, H= 0.6)))
    }

    val synapses : ArrayList<SynapseObject> = arrayListOf()

    for(i in 0 until excCount)
    {
        for(j in 0 until excCount)
        {
            if(i != j)
            {
                val synapse = SynapseObject(excCells[i], excCells[j])
                synapses.add(synapse)
            }
        }
    }

    for(i in 0 until excCount)
    {
        for(j in 0 until inhCount)
        {
            val synapse = SynapseObject(excCells[i], inhibCells[j])
            synapses.add(synapse)
        }
    }

    for(i in 0 until inhCount)
    {
        for(j in 0 until inhCount)
        {
            if(i != j)
            {
                val synapse = SynapseObject(inhibCells[i], inhibCells[j], isInhibitory = true)
                synapses.add(synapse)
            }
        }
    }
    for(i in 0 until inhCount)
    {
        for(j in 0 until excCount)
        {
            val synapse = SynapseObject(inhibCells[i], excCells[j], isInhibitory = true)
            synapses.add(synapse)
        }
    }

    val allObjects : ArrayList<ModelObject> = arrayListOf()
    allObjects.addAll(inhibCells)
    allObjects.addAll(excCells)
    allObjects.addAll(synapses)

    val config = Configuration()

    val pathwayDynamic: Pathway<HHCellObject> = Pathway()
    pathwayDynamic.add(HHCellObject::IDynamicMechanism, SimpleParameters(1.0), 2) { true }
    pathwayDynamic.add(HHCellObject::VDynamicMechanism, SimpleParameters(1.0), 2) { true }
    pathwayDynamic.add(HHCellObject::NDynamicMechanism, SimpleParameters(1.0), 2) { true }
    pathwayDynamic.add(HHCellObject::MDynamicMechanism, SimpleParameters(1.0), 2) { true }
    pathwayDynamic.add(HHCellObject::HDynamicMechanism, SimpleParameters(1.0), 2) { true }

    val pathwaySynapse: Pathway<SynapseObject> = Pathway()
    pathwaySynapse.add(SynapseObject::spikeTransferMechanism, SimpleParameters(1.0), 2) {true}
    pathwaySynapse.add(SynapseObject::synapseDecayMechanism, SimpleParameters(1.0), 10) {true}

    config.add(SynapseObject::class, arrayListOf(pathwaySynapse))
    config.add(HHCellObject::class, arrayListOf(pathwayDynamic))


    val s = Model(allObjects, config)

    val elapsed = measureTimeMillis {
        s.simulate { it.currentTime() > 100_00 }
    }
    println("Time taken: $elapsed")
    println("Already calculated")

}