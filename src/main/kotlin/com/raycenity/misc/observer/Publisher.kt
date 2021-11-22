package com.raycenity.misc.observer

import com.raycenity.misc.enm.EnumMap
import java.util.*

class Publisher<Event> : Observable<Event> {
    private val observers: MutableMap<ObserverPriority, WeakHashMap<Any, (Event) -> Unit>> = mutableMapOf()

    override fun subscribe(observer: Any, priority: ObserverPriority, handler: (Event) -> Unit) {
        require(observers[priority]?.containsKey(observer) != true) { "observer already subscribed with this priority" }
        observers.getOrPut(priority, ::WeakHashMap)[observer] = handler
    }

    override fun unsubscribe(observer: Any, priority: ObserverPriority) {
        require(observers[priority]?.containsKey(observer) == true) { "observer not subscribed with this priority" }
        observers[priority]!!.remove(observer)
    }

    fun publish(event: Event) {
        for (observersForPriority in observers.values) {
            // We need to prevent concurrent modification
            val observersForPrioritySnapshot = observersForPriority.toMap()
            for (handler in observersForPrioritySnapshot.values) {
                handler(event)
            }
        }
    }
}

fun Publisher<Unit>.publish() = publish(Unit)
