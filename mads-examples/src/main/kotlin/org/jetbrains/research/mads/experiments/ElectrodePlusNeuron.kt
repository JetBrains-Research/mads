package org.jetbrains.research.mads.experiments

import org.jetbrains.research.mads.core.configuration.Configuration
import org.jetbrains.research.mads.core.simulation.Model
import org.jetbrains.research.mads.core.telemetry.FileSaver
import org.jetbrains.research.mads.core.types.ModelObject
import org.jetbrains.research.mads.ns.electrode.Electrode
import org.jetbrains.research.mads.ns.pathways.connectToCell
import org.jetbrains.research.mads.ns.physiology.neurons.Neuron
import kotlin.io.path.Path
import kotlin.reflect.KProperty

fun experimentWithElectrodeAndNeuron(experimentName: String,
                                     logFolder: String,
                                     logSignals: List<KProperty<*>>,
                                     electrodeFun: () -> Electrode,
                                     neuronFun: () -> Neuron,
                                     config: Configuration,
                                     time: Double) {

    val dir = Path("log/$experimentName/OneNeuron/${logFolder}")
    val saver = FileSaver(dir)
    logSignals.forEach { saver.addSignalsNames(it) }

    val objects: ArrayList<ModelObject> = arrayListOf()
    val cell = neuronFun()
    val electrode = electrodeFun()
    cell.type = "neuron"
    electrode.type = "electrode"
    electrode.connectToCell(cell)
    objects.add(cell)
    objects.add(electrode)

    val s = Model(objects, config)
    val stopTime = (time.toBigDecimal() / config.timeResolution.toBigDecimal()).toLong()
    s?.simulate(saver) { it.nextTime() > stopTime }
    saver.closeModelWriters()
}