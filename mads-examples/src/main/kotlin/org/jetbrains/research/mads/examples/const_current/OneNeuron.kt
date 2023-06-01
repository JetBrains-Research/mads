package org.jetbrains.research.mads.examples.const_current

import org.jetbrains.research.mads.core.configuration.Always
import org.jetbrains.research.mads.core.configuration.Configuration
import org.jetbrains.research.mads.core.configuration.configure
import org.jetbrains.research.mads.core.configuration.pathway
import org.jetbrains.research.mads.core.simulation.Model
import org.jetbrains.research.mads.core.telemetry.FileSaver
import org.jetbrains.research.mads.core.types.ModelObject
import org.jetbrains.research.mads.core.types.microsecond
import org.jetbrains.research.mads.core.types.millisecond
import org.jetbrains.research.mads.ns.electrode.Electrode
import org.jetbrains.research.mads.ns.electrode.connectToCell
import org.jetbrains.research.mads.ns.overThresholdAndNotSpiked
import org.jetbrains.research.mads.ns.underThresholdAndSpiked
import org.jetbrains.research.mads.ns.physiology.neurons.*
import java.util.*
import kotlin.io.path.Path
import kotlin.reflect.KProperty

fun main() {
    val experimentName = "const_current"
    val currents = arrayOf(5.0, 10.0, 20.0, 30.0, 50.0)
    val startTime = System.currentTimeMillis()
    val modelingTime = 500 * millisecond
    val randomSeed = 12345L
    val logSignals = arrayListOf<KProperty<*>>(
        SpikesSignals::spiked,
        PotentialSignals::V,
        CurrentSignals::I_e
    )
    println("Experiment start time $startTime")

    for (current in currents) {
        println("Experiments with $current nA current")
        experimentWithElectrodeAndNeuron(
            experimentName, "${current}_microA/lif/${startTime}",
            logSignals,
            { -> Electrode(Random(randomSeed), CurrentSignals(I_e = current)) },
            { -> LIFNeuron(LIFConstants.V_thresh) },
            configure {
                timeResolution = microsecond
                addPathway(pathway<LIFNeuron> {
                    timeResolution = microsecond
                    mechanism(mechanism = LIFMechanisms.VDynamic) {
                        duration = 100
                        condition = Always
                    }
                    mechanism(mechanism = NeuronMechanisms.SpikeOn) {
                        duration = 100
                        condition = { overThresholdAndNotSpiked(it) }
                    }
                    mechanism(mechanism = NeuronMechanisms.SpikeOff) {
                        duration = 100
                        condition = { underThresholdAndSpiked(it) }
                    }
                    mechanism(mechanism = NeuronMechanisms.SpikeTransfer) {
                        duration = 100
                        condition = { overThresholdAndNotSpiked(it) }
                        constants = SpikeTransferConstants()
                    }
                })
            },
            modelingTime
        )
        experimentWithElectrodeAndNeuron(
            experimentName, "${current}_microA/lif/${startTime}",
            logSignals, { -> Electrode(Random(randomSeed), CurrentSignals(I_e = current)) },
            { -> IzhNeuron() },
            configure {
                timeResolution = microsecond
                addPathway(pathway<IzhNeuron> {
                    timeResolution = microsecond
                    mechanism(mechanism = IzhMechanisms.Dynamic) {
                        duration = 500
                        condition = Always
                    }
                    mechanism(mechanism = NeuronMechanisms.SpikeOff) {
                        duration = 1
                        condition = { underThresholdAndSpiked(it) }
                    }
                    mechanism(mechanism = NeuronMechanisms.SpikeTransfer) {
                        duration = 1
                        condition = { overThresholdAndNotSpiked(it) }
                        constants = SpikeTransferConstants(I_transfer = 1.0)
                    }
                })
            },
            modelingTime
        )
        experimentWithElectrodeAndNeuron(
            experimentName, "${current}_microA/lif/${startTime}",
            logSignals,
            { -> Electrode(Random(randomSeed), CurrentSignals(I_e = current)) },
            { -> HHNeuron(HHConstants.V_thresh, HHSignals()) },
            configure {
                timeResolution = microsecond
                addPathway(pathway<HHNeuron> {
                    timeResolution = microsecond
                    mechanism(mechanism = HHMechanisms.VDynamic) {
                        duration = 25
                        condition = Always
                    }
                    mechanism(mechanism = HHMechanisms.NDynamic) {
                        duration = 25
                        condition = Always
                    }
                    mechanism(mechanism = HHMechanisms.MDynamic) {
                        duration = 25
                        condition = Always
                    }
                    mechanism(mechanism = HHMechanisms.HDynamic) {
                        duration = 25
                        condition = Always
                    }
                    mechanism(mechanism = NeuronMechanisms.SpikeOn) {
                        duration = 25
                        condition = { overThresholdAndNotSpiked(it) }
                    }
                    mechanism(mechanism = NeuronMechanisms.SpikeOff) {
                        duration = 25
                        condition = { underThresholdAndSpiked(it) }
                    }
                    mechanism(mechanism = NeuronMechanisms.SpikeTransfer) {
                        duration = 25
                        condition = { overThresholdAndNotSpiked(it) }
                        constants = SpikeTransferConstants()
                    }
                })
            },
            modelingTime
        )
    }
}

fun experimentWithElectrodeAndNeuron(
    experimentName: String,
    logFolder: String,
    logSignals: List<KProperty<*>>,
    electrodeFun: () -> Electrode,
    neuronFun: () -> Neuron,
    config: Configuration,
    time: Double
) {

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