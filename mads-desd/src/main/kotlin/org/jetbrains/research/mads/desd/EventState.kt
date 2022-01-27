package org.jetbrains.research.mads.desd

enum class EventState {
    Waiting,
    WaitingInQueue,
    Active,
    Postponed,
    Ready
}