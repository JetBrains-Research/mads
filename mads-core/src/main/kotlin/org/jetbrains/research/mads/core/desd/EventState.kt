package org.jetbrains.research.mads.core.desd

enum class EventState {
    Waiting,
    WaitingInQueue,
    Active,
    Postponed,
    Ready
}