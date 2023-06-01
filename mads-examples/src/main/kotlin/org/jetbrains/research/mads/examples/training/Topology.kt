package org.jetbrains.research.mads.examples.training

import org.jetbrains.research.mads.core.types.ModelObject
import org.jetbrains.research.mads.ns.*
import org.jetbrains.research.mads.ns.physiology.neurons.InputNeuron2DGrid
import org.jetbrains.research.mads.ns.physiology.neurons.Neuron
import org.jetbrains.research.mads.ns.physiology.neurons.STDPTripletSignals
import org.jetbrains.research.mads.ns.physiology.synapses.SynapseSignals
import org.jetbrains.research.mads.providers.MnistProvider
import java.util.*

data class SynapsesParameters(
    val weight: () -> Double,
    val delay: () -> Int,
)

class Topology {
    companion object {
        const val INPUT_LAYER = "inputLayer"
        const val SECOND_LAYER = "secondLayer"
        const val OUTPUT_LAYER = "thirdLayer"

        fun mnistTopology(
            provider: MnistProvider,
            excNeuronFun: () -> Neuron,
            inhNeuronFun: () -> Neuron,
            nExc: Int,
            rnd: Random,
            synapsesParameters: List<SynapsesParameters>
        ): List<ModelObject> {
            val inputNeuron2DGrid = InputNeuron2DGrid(provider, 10.0)
            inputNeuron2DGrid.type = INPUT_LAYER
            val secondLayer: List<Neuron> = createPopulation(nExc, SECOND_LAYER, excNeuronFun)
            val thirdLayer: List<Neuron> = createPopulation(nExc, OUTPUT_LAYER, inhNeuronFun)

            val synapses1to2 = connectPopulations(
                inputNeuron2DGrid.getNeurons(),
                secondLayer,
                probability = 1.0,
                rnd = rnd,
                weight = synapsesParameters[0].weight,
                delay = synapsesParameters[0].delay
            )
            val synapses2to3 = connectPopulationsOneToOne(
                secondLayer,
                thirdLayer,
                weight = synapsesParameters[1].weight,
                delay = synapsesParameters[1].delay
            )
            val synapses3to2 = connectPopulationsInhibition(
                thirdLayer,
                secondLayer,
                weight = synapsesParameters[2].weight,
                delay = synapsesParameters[2].delay
            )

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

        fun excitatorySimple(
            inputNeuronFn: () -> Neuron,
            excNeuronFun: () -> Neuron
        ): List<ModelObject> {
            val inputn = inputNeuronFn()
            val ne = excNeuronFun()

            val syn = connectCellsWithSynapse(
                inputn,
                ne,
                false,
                SynapseSignals(weight = 20.0, delay = 0, maxWeight = 30.0),
                STDPTripletSignals(),
            )

            inputn.type = "input"
            ne.type = "inter"
            syn.type = "syn"

            return listOf(
                inputn,
                ne,
                syn
            )
        }

        fun inhibitorySimple(
            inputNeuronFn: () -> Neuron,
            excNeuronFun: () -> Neuron,
            inhNeuronFun: () -> Neuron,
        ): List<ModelObject> {
            val inputn = inputNeuronFn()
            val ne = excNeuronFun()
            val ni = inhNeuronFun()

            val syn = connectCellsWithSynapse(
                inputn,
                ne,
                false,
                SynapseSignals(weight = 20.0, delay = 0, maxWeight = 30.0),
                STDPTripletSignals(),
            )

            val synei = connectCellsWithSynapse(
                ne,
                ni,
                false,
                SynapseSignals(weight = 15.0, delay = 0, maxWeight = 1.0, learningEnabled = false),
                STDPTripletSignals(),
            )

            val synie = connectCellsWithSynapse(
                ni,
                ne,
                true,
                SynapseSignals(weight = 20.0, delay = 0, maxWeight = 1.0, learningEnabled = false),
                STDPTripletSignals(),
            )

            inputn.type = "input"
            ne.type = "inter"
            ni.type = "inhib"
            syn.type = "syn_in"
            synei.type = "syn_ei"
            synie.type = "syn_ie"

            return listOf(
                inputn,
                ne,
                ni,
                syn,
                synei,
                synie
            )
        }
    }
}