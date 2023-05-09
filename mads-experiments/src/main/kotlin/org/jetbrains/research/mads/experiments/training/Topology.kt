package org.jetbrains.research.mads.experiments.training

import org.jetbrains.research.mads.core.types.ModelObject
import org.jetbrains.research.mads.ns.*
import org.jetbrains.research.mads.ns.electrode.ElectrodeArray
import org.jetbrains.research.mads.ns.physiology.neurons.Neuron
import org.jetbrains.research.mads.ns.physiology.synapses.Synapse
import org.jetbrains.research.mads.providers.MnistProvider
import java.util.*
import kotlin.math.sqrt

fun mnistTopology(
    provider: MnistProvider,
    excNeuronFun: () -> Neuron,
    inhNeuronFun: () -> Neuron,
    nExc: Int
): List<ModelObject> {
    val electrodesArray = ElectrodeArray(provider, 10.0)
    val firstLayer: List<Neuron> = createPopulation(electrodesArray.capacity(), "firstLayer", excNeuronFun)
    val secondLayer: List<Neuron> = createPopulation(nExc, "secondLayer", excNeuronFun)
    val thirdLayer: List<Neuron> = createPopulation(nExc, "thirdLayer", inhNeuronFun)

    connectElectrodeArray(electrodesArray, firstLayer)

    val rnd = Random(42L)

    val synapses: List<Synapse> = connectPopulations(firstLayer, secondLayer) { sqrt(rnd.nextDouble() * 9) }
    val synapses2to3: List<Synapse> = connectPopulationsOneToOne(secondLayer, thirdLayer) { sqrt(rnd.nextDouble() * 9) }
    val synapses3to2: List<Synapse> =
        connectPopulationsInhibition(thirdLayer, secondLayer) { sqrt(rnd.nextDouble() * 9) }

    val objects: ArrayList<ModelObject> = arrayListOf()
    objects.add(electrodesArray)
    objects.addAll(electrodesArray.getChildElectrodes())
    objects.addAll(firstLayer)
    objects.addAll(secondLayer)
    objects.addAll(thirdLayer)
    objects.addAll(synapses)
    objects.addAll(synapses2to3)
    objects.addAll(synapses3to2)

    return objects
}