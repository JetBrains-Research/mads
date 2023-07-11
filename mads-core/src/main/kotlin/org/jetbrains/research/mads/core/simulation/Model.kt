package org.jetbrains.research.mads.core.simulation

import kotlinx.serialization.Serializable
import org.jetbrains.research.mads.core.configuration.Configuration
import org.jetbrains.research.mads.core.configuration.RootObject
import org.jetbrains.research.mads.core.configuration.Structure
import org.jetbrains.research.mads.core.desd.EventsDispatcher
import org.jetbrains.research.mads.core.telemetry.EmptySaver
import org.jetbrains.research.mads.core.telemetry.ModelStateSerializer
import org.jetbrains.research.mads.core.telemetry.Saver
import org.jetbrains.research.mads.core.types.ModelObject
import java.util.stream.Collectors

@Serializable(with = ModelStateSerializer::class)
class Model private constructor(
    structure: Structure
) : ModelObject() {

    var tStart: Long = 0
    var currentTime: Long = 0
        private set

    private val dispatcher = EventsDispatcher()
    private val progressBar: ProgressBarRotating = ProgressBarRotating(250, "step: 0")

    init {
        println("Simulation step size is equal to ${configuration.timeResolution} seconds")
        parent = RootObject
        createEvents(configuration.getPathways(this::class))
        checkConditions()
        print("Adding objects and checking conditions... ")
        val groupedObjects = structure.getAllObjects().groupBy { it.first == RootObject }
        groupedObjects[true]?.forEach { this.addObject(it.second) }
        groupedObjects[false]?.forEach { it.first.addObject(it.second) }

        recursivelyGetChildObjects().forEach { it.checkConditions() }
        val allEvents = recursivelyGetChildObjects().map { it.events }.flatten()
        dispatcher.addEvents(allEvents)
        println("done")
    }

    fun simulate(saver: Saver = EmptySaver, stopCondition: (Model) -> Boolean) {
        progressBar.start()
        tStart = System.currentTimeMillis()
        var lastStep = 0L

        // 0. check S_i for stop condition -> stop or repeat from 1
        while (!stopCondition(this)) {

            // 1. process events from queue -> get grouped responses by model object
            currentTime = dispatcher.peekHead()
            val responses = dispatcher.calculateNextTick()

            // 2. apply responses to each object independently -> S_i to S_i+1
            val updatedObjects = responses.entries.parallelStream()
                .map { e -> e.key.applyResponses(e.value) }
                .flatMap { it.stream() }
                .distinct()
                .collect(Collectors.toList())

            // 3. calculate conditions -> map of events
            updatedObjects.parallelStream()
                .forEach {
                    saver.logChangedState(currentTime, it)
                    it.checkConditions()
                }

            // 4. update events in queue -> S_t
            val allEvents = updatedObjects.parallelStream()
                .map { it.events }
                .flatMap { it.stream() }
                .collect(Collectors.toList())

            dispatcher.addEvents(allEvents)

            val realTime = System.currentTimeMillis() - tStart
            val extInfo = "step: ${"%,d".format(currentTime)}"
            lastStep = currentTime
            progressBar.updateInfo(realTime, extInfo)
        }
        progressBar.stop("done")
        val totalModelingTime = configuration.timeResolution.toBigDecimal().multiply(lastStep.toBigDecimal()).toDouble()
        println("Total of $totalModelingTime seconds were simulated")
        finalize()
        println("Configuration was unload from model")
        println("Elapsed time reset to zero")
        println("Events were cleared in each object\n")
    }

    fun nextTime() : Long {
        return dispatcher.peekHead()
    }

    internal fun getCurrentTime() : Long {
        return currentTime
    }

    private fun finalize() {
        configuration = Configuration()
        getCurrentTime = { 0 }

        getChildObjects().forEach {
            it.events.clear()
        }
    }

    companion object {
        operator fun invoke(structure: Structure, configuration: Configuration): Model? {
            if (configuration.hasErrors()) {
                configuration.errors().forEach { println(it) }
                return null
            }

            if (structure.isEmpty()) {
                println("Nothing to simulate. Initial model state does not contain any objects.")
                return null
            }

            ModelObject.configuration = configuration
            val model = Model(structure)
            getCurrentTime = model::getCurrentTime

            return model
        }

        fun timeStopCondition(threshold: Long): (Model) -> Boolean {
            return fun(model: Model): Boolean {
                return model.nextTime() > threshold
            }
        }
    }
}