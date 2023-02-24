package org.jetbrains.research.mads.core.types

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class TimeResolutionAnnotation(val resolution: Double = second)