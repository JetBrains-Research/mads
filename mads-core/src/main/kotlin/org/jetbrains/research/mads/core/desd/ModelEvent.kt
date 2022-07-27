package org.jetbrains.research.mads.core.desd

import org.jetbrains.research.mads.core.types.ModelObject
import org.jetbrains.research.mads.core.types.Response
import java.util.*

typealias ProcessState = () -> Array<Response>

object EmptyResponse {
    val value : Array<Response> = arrayOf()
}

class ModelEvent<out MO : ModelObject>(
    private val reference: (MO) -> Array<Response>,
    private val eventObject: MO,
    private val duration: Int,
    seed: Long = 12345L,
    rangePercent: Double = 0.1
) {

    private val rnd: Random
    private val deltaRange: Double
    private var eventTime: Long
    private var postponeTime: Long
    private var eventState: EventState

    private val stateProcessorMap: EnumMap<EventState, ProcessState> =
        EnumMap<EventState, ProcessState>(mapOf<EventState, ProcessState>(
            EventState.Active to this::processActiveEvent,
            EventState.Postponed to this::processPostponedEvent,
            EventState.WaitingInQueue to this::processWaitingInQueueEvent))

    init {
        rnd = Random(seed)
        deltaRange = duration * rangePercent
        eventTime = 0
        postponeTime = 0
        eventState = EventState.Waiting
    }

    fun getEventTime(): Long = eventTime

    fun executeEvent(): Array<Response> = stateProcessorMap[eventState]!!.invoke()

    fun updateTime(tick: Long): Boolean {
        var result = true
        if (eventState === EventState.Ready) {
            eventTime = withDelta(tick) + duration
            eventState = EventState.Active
        } else if (eventState === EventState.WaitingInQueue) {
            postponeTime = withDelta(tick) + duration
            eventState = EventState.Postponed
        } else {
            result = false
        }
        return result
    }

    fun prepareEvent() {
        if (eventState === EventState.Waiting) eventState = EventState.Ready
    }

    fun disruptEvent() {
        if (eventState === EventState.Active || eventState === EventState.Postponed)
            eventState = EventState.WaitingInQueue
        else if (eventState === EventState.Ready)
            eventState = EventState.Waiting
    }

    private fun processActiveEvent(): Array<Response> {
        eventState = EventState.Waiting
        return reference(eventObject)
    }

    private fun processPostponedEvent(): Array<Response> {
        eventTime = postponeTime
        eventState = EventState.Active
        return EmptyResponse.value
    }

    private fun processWaitingInQueueEvent(): Array<Response> {
        eventState = EventState.Waiting
        return EmptyResponse.value
    }

    private fun withDelta(tick: Long): Long {
        return tick + (rnd.nextGaussian() * deltaRange).toLong()
    }
}