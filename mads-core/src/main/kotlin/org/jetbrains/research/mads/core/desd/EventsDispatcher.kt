package org.jetbrains.research.mads.core.desd

import org.jetbrains.research.mads.core.types.ModelObject
import org.jetbrains.research.mads.core.types.Response
import java.util.*
import java.util.stream.Collectors

class EventsDispatcher<M : ModelObject> {

    private var currentTick: Long = 0
    private val eventsDic: MutableMap<Long, MutableList<ModelEvent<M>>> = mutableMapOf()
    private val eventTime: Queue<Long> = PriorityQueue()

    private val emptyAnswer: Array<Response> = emptyArray()

    fun getCurrentTick(): Long {
        return currentTick
    }

    fun addEvents(modelEvents: Array<ModelEvent<M>>) {
        val groupedEvents: Map<Long, List<ModelEvent<M>>> = Arrays.stream(modelEvents)
            .parallel()
            .filter { it.updateTime(currentTick) }
            .collect(Collectors.groupingBy(ModelEvent<M>::getEventTime))

        val ticks = groupedEvents.keys
            .filter { tick: Long? -> !eventsDic.contains(tick) }
            .toList()

        groupedEvents.forEach {
            eventsDic.putIfAbsent(it.key, ArrayList())
            eventsDic[it.key]!!.addAll(it.value)
        }

        eventTime.addAll(ticks)
    }

    fun calculateNextTick(): Array<Response> {
        if (eventTime.isEmpty()) return emptyAnswer

        currentTick = eventTime.remove()
        val currentEvents: MutableList<ModelEvent<M>> = eventsDic.remove(currentTick)!!

        return currentEvents.parallelStream()
            .map(ModelEvent<M>::executeEvent)
            .filter { it != EmptyResponse }
            .toArray { arrayOf() }
    }

    fun peekHead(): Long {
        return if (eventTime.peek() != null) eventTime.peek() else Long.MAX_VALUE
    }
}
