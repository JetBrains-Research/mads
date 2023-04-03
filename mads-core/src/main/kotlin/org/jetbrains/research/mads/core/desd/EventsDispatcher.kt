package org.jetbrains.research.mads.core.desd

import org.jetbrains.research.mads.core.types.ModelObject
import org.jetbrains.research.mads.core.types.Response
import java.util.*
import java.util.stream.Collectors

class EventsDispatcher {

    private var currentTick: Long = 0
    private val eventsDic: HashMap<Long, HashSet<ModelEvent>> = hashMapOf()
    private val eventTime: Queue<Long> = PriorityQueue()

    fun addEvents(modelEvents: List<ModelEvent>) {
        val groupedEvents: Map<Long, List<ModelEvent>> = modelEvents.parallelStream()
            .filter { it.updateTime(currentTick) }
            .collect(Collectors.groupingBy(ModelEvent::getEventTime))

        val ticks = groupedEvents.keys
            .filter { tick: Long? -> !eventsDic.contains(tick) }
            .toList()

        groupedEvents.forEach {
            eventsDic.putIfAbsent(it.key, HashSet())
            eventsDic[it.key]!!.addAll(it.value)
        }

        eventTime.addAll(ticks)
    }

    fun calculateNextTick(): Map<ModelObject, List<Response>> {
        if (eventTime.isEmpty()) return mapOf()

        currentTick = eventTime.remove()
        val currentEvents: Set<ModelEvent> = eventsDic.remove(currentTick)!!

        return currentEvents.parallelStream()
            .map(ModelEvent::executeEvent)
            .flatMap { it.stream() }
            .collect(Collectors.groupingBy(Response::sourceObject))
    }

    fun peekHead(): Long {
        return if (eventTime.peek() != null) eventTime.peek() else Long.MAX_VALUE
    }
}
