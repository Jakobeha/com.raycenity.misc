package com.raycenity.misc.observer

/**
 * A dynamic value: this consists of a getter,
 * and an observer which is guaranteed to fire whenever the getter's result changes
 */
data class ObservedValue<T>(val didChange: Observable<Any>, val get: () -> T)
