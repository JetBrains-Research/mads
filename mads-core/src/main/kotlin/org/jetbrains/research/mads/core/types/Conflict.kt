package org.jetbrains.research.mads.core.types

class Conflict(
    val conflictSubject: ConflictSubject,
    val resolve: (List<Response>) -> List<Response>
)

interface ConflictSubject

object NoConflictSubject : ConflictSubject

val noConflict: Conflict = Conflict(NoConflictSubject) { responses -> responses }