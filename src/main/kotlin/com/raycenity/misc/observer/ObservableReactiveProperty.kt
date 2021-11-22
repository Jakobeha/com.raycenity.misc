package com.raycenity.misc.observer

class ObservableReactiveProperty<P>(
    private val myPublisher: Publisher<P>,
    observerPriority: ObserverPriority,
    vararg observables: Observable<*>,
    compute: (P?) -> P
) :
    ReactiveProperty<P>(observerPriority, observables = observables, compute) {
    override var backing: P = compute(null)
        set(newValue) {
            val oldBacking = backing
            field = newValue
            myPublisher.publish(oldBacking)
        }
}
