package org.jetbrains.research.mads.core.desd

import org.jetbrains.research.mads.core.types.ModelObject
import org.jetbrains.research.mads.core.types.Response
import java.util.*
import java.util.stream.Collectors

class EventsDispatcher {

    private var currentTick: Long = 0
    private val eventsDic: MutableMap<Long, MutableList<ModelEvent>> = mutableMapOf()
    private val eventTime: Queue<Long> = PriorityQueue()

    fun getCurrentTick(): Long {
        return currentTick
    }

    fun addEvents(modelEvents: List<ModelEvent>) {
        val groupedEvents: Map<Long, List<ModelEvent>> = modelEvents.parallelStream()
            .filter { it.updateTime(currentTick) }
            .collect(Collectors.groupingBy(ModelEvent::getEventTime))

        val ticks = groupedEvents.keys
            .filter { tick: Long? -> !eventsDic.contains(tick) }
            .toList()

        groupedEvents.forEach {
            eventsDic.putIfAbsent(it.key, ArrayList())
            eventsDic[it.key]!!.addAll(it.value)
        }

        eventTime.addAll(ticks)
    }

    fun calculateNextTick(): Map<ModelObject, List<Response>> {
        if (eventTime.isEmpty()) return mapOf()

        currentTick = eventTime.remove()
        val currentEvents: MutableList<ModelEvent> = eventsDic.remove(currentTick)!!

        return currentEvents.parallelStream()
            .map(ModelEvent::executeEvent)
            .flatMap { it.stream() }
            .collect(Collectors.groupingBy(Response::sourceObject))
    }

    fun peekHead(): Long {
        return if (eventTime.peek() != null) eventTime.peek() else Long.MAX_VALUE
    }
}
