package com.raycenity.misc.observer

class MappingObservable<in I, out O> private constructor(
    inputObservers: Iterable<Observable<I>>,
    inputPriority: ObserverPriority,
    transformer: (I) -> O,
    private val backing: Publisher<O>
) : Observable<O> by backing {
    companion object {
        operator fun <O> invoke(
            inputObservers: Iterable<Observable<O>>,
            inputPriority: ObserverPriority
        ): MappingObservable<O, O> = MappingObservable(inputObservers, inputPriority) { it }

        operator fun <O> invoke(
            inputPriority: ObserverPriority,
            vararg inputObservers: Observable<O>
        ): MappingObservable<O, O> = MappingObservable(inputObservers.toList(), inputPriority)

    }

    init {
        for (inputObserver in inputObservers) {
            inputObserver.subscribe(this, inputPriority) { input ->
                val output = transformer(input)
                backing.publish(output)
            }
        }
    }

    constructor(inputObservers: Iterable<Observable<I>>, inputPriority: ObserverPriority, transformer: (I) -> O) :
            this(inputObservers, inputPriority, transformer, Publisher())

    constructor(inputPriority: ObserverPriority, vararg inputObservers: Observable<I>, transformer: (I) -> O) :
            this(inputObservers.toList(), inputPriority, transformer)
}

