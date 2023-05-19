package org.jetbrains.research.mads.core.desd

import org.jetbrains.research.mads.core.types.EmptyResponseList
import org.jetbrains.research.mads.core.types.Response
import java.util.*
import kotlin.system.exitProcess

typealias ProcessState = () -> List<Response>

class ModelEvent(
    private val mechanism: () -> List<Response>,
    private val condition: () -> Boolean,
    private val duration: Int,
    private val delay: () -> Int,
    seed: Long = 12345L,
    rangePercent: Double = 0.0
) {

    private val rnd: Random
    private val deltaRange: Double
    private var eventTime: Long
    private var postponeTime: Long
    private var eventState: EventState

    private val exceptionHandler = Thread.UncaughtExceptionHandler { _, throwable ->
        println("\nException: ${throwable.message}")
        exitProcess(1)
    }

    private val stateProcessorMap: EnumMap<EventState, ProcessState> =
        EnumMap<EventState, ProcessState>(
            mapOf<EventState, ProcessState>(
                EventState.Active to this::processActiveEvent,
                EventState.Postponed to this::processPostponedEvent,
                EventState.WaitingInQueue to this::processWaitingInQueueEvent
            )
        )

    init {
        rnd = Random(seed)
        deltaRange = duration * rangePercent
        eventTime = 0
        postponeTime = 0
        eventState = EventState.Waiting
    }

    fun getEventTime(): Long = eventTime

    fun executeEvent(): List<Response> {
        val result: List<Response>

        try {
            Thread.setDefaultUncaughtExceptionHandler(exceptionHandler)
            result = stateProcessorMap[eventState]!!.invoke()
        } finally {
            Thread.setDefaultUncaughtExceptionHandler(Thread.getDefaultUncaughtExceptionHandler())
        }

        return result
    }

    fun updateTime(tick: Long): Boolean {
        var result = true
        val delay = 0.coerceAtLeast(delay())
        if (eventState === EventState.Ready) {
            eventTime = withDelta(tick) + duration + delay
            eventState = EventState.Active
        } else if (eventState === EventState.WaitingInQueue) {
            postponeTime = withDelta(tick) + duration + delay
            eventState = EventState.Postponed
        } else {
            result = false
        }
        return result
    }

    fun checkCondition(): Boolean {
        return condition()
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

    private fun processActiveEvent(): List<Response> {
        eventState = EventState.Waiting
        return mechanism()
    }

    private fun processPostponedEvent(): List<Response> {
        eventTime = postponeTime
        eventState = EventState.Active
        return EmptyResponseList
    }

    private fun processWaitingInQueueEvent(): List<Response> {
        eventState = EventState.Waiting
        return EmptyResponseList
    }

    private fun withDelta(tick: Long): Long {
        return tick + (rnd.nextGaussian() * deltaRange).toLong()
    }
}