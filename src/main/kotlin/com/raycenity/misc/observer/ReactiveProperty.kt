package com.raycenity.misc.observer

import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

open class ReactiveProperty<P>(
    observerPriority: ObserverPriority,
    vararg observables: Observable<*>,
    compute: (P?) -> P
) :
    ReadOnlyProperty<Any, P> {
    protected open var backing: P = compute(null)

    init {
        for (observable in observables) {
            @Suppress("LeakingThis")
            observable.subscribe(this, observerPriority) {
                backing = compute(backing)
            }
        }
    }

    final override operator fun getValue(thisRef: Any, property: KProperty<*>): P = backing
}
