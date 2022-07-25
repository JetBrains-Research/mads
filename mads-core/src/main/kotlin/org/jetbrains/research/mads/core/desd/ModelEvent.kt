package org.jetbrains.research.mads.core.desd

import org.jetbrains.research.mads.core.types.ModelObject
import org.jetbrains.research.mads.core.types.Response
import java.util.*

typealias ProcessState = () -> Response
typealias ModelFunction<T> = T.(Long) -> Response

object EmptyResponse : Response

class ModelEvent<M: ModelObject>(
    private val reference: ModelFunction<M>,
    private val eventObject: M,
    private val duration: Int,
    seed: Long,
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

    fun executeEvent(): Response = stateProcessorMap[eventState]!!.invoke()

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

    private fun processActiveEvent(): Response {
        eventState = EventState.Waiting
        return reference.invoke(eventObject, eventTime)
    }

    private fun processPostponedEvent(): Response {
        eventTime = postponeTime
        eventState = EventState.Active
        return EmptyResponse
    }

    private fun processWaitingInQueueEvent(): Response {
        eventState = EventState.Waiting
        return EmptyResponse
    }

    private fun withDelta(tick: Long): Long {
        return tick + (rnd.nextGaussian() * deltaRange).toLong()
    }
}