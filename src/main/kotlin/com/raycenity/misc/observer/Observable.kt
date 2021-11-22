package com.raycenity.misc.observer

interface Observable<out Event> {
  fun subscribe(
    observer: Any,
    priority: ObserverPriority = ObserverPriority.DEFAULT,
    handler: (Event) -> Unit
  )
  fun unsubscribe(
    observer: Any,
    priority: ObserverPriority = ObserverPriority.DEFAULT
  )
}
