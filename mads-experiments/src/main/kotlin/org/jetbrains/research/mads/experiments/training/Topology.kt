package org.jetbrains.research.mads.experiments.training

import org.jetbrains.research.mads.core.types.ModelObject
import org.jetbrains.research.mads.ns.connectPopulations
import org.jetbrains.research.mads.ns.connectPopulationsInhibition
import org.jetbrains.research.mads.ns.connectPopulationsOneToOne
import org.jetbrains.research.mads.ns.createPopulation
import org.jetbrains.research.mads.ns.physiology.neurons.InputNeuron2DGrid
import org.jetbrains.research.mads.ns.physiology.neurons.Neuron
import org.jetbrains.research.mads.providers.MnistProvider
import java.util.*

fun mnistTopology(
    provider: MnistProvider,
    excNeuronFun: () -> Neuron,
    inhNeuronFun: () -> Neuron,
    nExc: Int
): List<ModelObject> {
    val inputNeuron2DGrid = InputNeuron2DGrid(provider, 10.0)
    inputNeuron2DGrid.type = "inputLayer"
    val secondLayer: List<Neuron> = createPopulation(nExc, "secondLayer", excNeuronFun)
    val thirdLayer: List<Neuron> = createPopulation(nExc, "thirdLayer", inhNeuronFun)

    val rnd = Random(42L)

    val synapses1to2 =
        connectPopulations(inputNeuron2DGrid.getNeurons(), secondLayer, probability = 1.0, rnd = rnd,
            weight = { rnd.nextDouble() / 2 }, delay = { rnd.nextInt(100) })
    val synapses2to3 = connectPopulationsOneToOne(secondLayer, thirdLayer, weight = { 5.0 }, delay = { 0 })
    val synapses3to2 = connectPopulationsInhibition(thirdLayer, secondLayer, weight = { 5.0 }, delay = { 0 })

    val objects: ArrayList<ModelObject> = arrayListOf()
    objects.add(inputNeuron2DGrid)
    objects.addAll(inputNeuron2DGrid.getNeurons())
    objects.addAll(secondLayer)
    objects.addAll(thirdLayer)
    objects.addAll(synapses1to2)
    objects.addAll(synapses2to3)
    objects.addAll(synapses3to2)

    return objects
}