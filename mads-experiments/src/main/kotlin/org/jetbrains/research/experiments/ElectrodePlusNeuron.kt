package org.jetbrains.research.experiments

import org.jetbrains.research.mads.core.configuration.Configuration
import org.jetbrains.research.mads.core.simulation.Model
import org.jetbrains.research.mads.core.telemetry.FileSaver
import org.jetbrains.research.mads.core.types.ModelObject
import org.jetbrains.research.mads_ns.electrode.Electrode
import org.jetbrains.research.mads_ns.pathways.connectToCell
import org.jetbrains.research.mads_ns.physiology.neurons.CurrentSignals
import org.jetbrains.research.mads_ns.physiology.neurons.Neuron
import java.util.*
import kotlin.io.path.Path
import kotlin.reflect.KProperty

fun experimentWithElectrodeAndNeuron(experimentName: String,
                                     logFolder: String,
                                     logSignals: List<KProperty<*>>,
                                     initialCurrent: Double,
                                     neuronFun: () -> Neuron,
                                     config: Configuration,
                                     time: Double,
                                     seed: Long) {

    val dir = Path("log/$experimentName/OneNeuron/${logFolder}")
    val saver = FileSaver(dir)
    logSignals.forEach { saver.addSignalsNames(it) }

    val rnd = Random(seed)

    val objects: ArrayList<ModelObject> = arrayListOf()
    val cell = neuronFun()
    val electrode = Electrode(rnd, CurrentSignals(I_e = initialCurrent))
    cell.type = "neuron"
    electrode.type = "electrode"
    electrode.connectToCell(cell)
    objects.add(cell)
    objects.add(electrode)

    val s = Model(objects, config)
    val stopTime = (time.toBigDecimal() / config.timeResolution.toBigDecimal()).toLong()
    s?.simulate(saver) { it.currentTime() > stopTime }
    saver.closeModelWriters()
}