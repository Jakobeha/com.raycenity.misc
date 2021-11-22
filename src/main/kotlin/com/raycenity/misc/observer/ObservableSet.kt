package com.raycenity.misc.observer

class ObservableSet<T> private constructor(private val backing: MutableSet<T>) :
  Set<T> by backing,
  MutableSet<T> {
  private inner class Iterator : MutableIterator<T> {
    private val backingIterator: MutableIterator<T> = backing.iterator()
    private var last: T? = null

    override fun hasNext(): Boolean = backingIterator.hasNext()
    override fun next(): T {
      last = backingIterator.next()
      return last!!
    }
    override fun remove() {
      backingIterator.remove()
      _didRemove.publish(last!!)
    }
  }

  private val _didAdd: Publisher<T> = Publisher()
  val didAdd: Observable<T>
    get() = _didAdd

  private val _didRemove: Publisher<T> = Publisher()
  val didRemove: Observable<T>
    get() = _didRemove

  constructor(initial: Iterable<T>) : this(initial.toMutableSet())

  override fun add(element: T): Boolean = backing.add(element).also { didAdd ->
    if (didAdd) {
      _didAdd.publish(element)
    }
  }

  override fun addAll(elements: Collection<T>): Boolean {
    var addedAnElement = false
    for (element in elements) {
      val addedThisElement = add(element)
      if (addedThisElement) {
        addedAnElement = true
      }
    }
    return addedAnElement
  }

  override fun clear() {
    val oldBacking = backing.toList()
    backing.clear()
    for (element in oldBacking) {
      _didRemove.publish(element)
    }
  }

  override fun remove(element: T): Boolean =
    backing.remove(element).also { didRemove ->
      if (didRemove) {
        _didRemove.publish(element)
      }
    }

  override fun removeAll(elements: Collection<T>): Boolean {
    val oldBacking = backing.toList()
    val removedAnElement = backing.retainAll(elements)

    for (element in oldBacking) {
      if (elements.contains(element)) {
        _didRemove.publish(element)
      }
    }

    return removedAnElement
  }

  override fun retainAll(elements: Collection<T>): Boolean {
    val oldBacking = backing.toList()
    val removedAnElement = backing.retainAll(elements)

    for (element in oldBacking) {
      if (!elements.contains(element)) {
        _didRemove.publish(element)
      }
    }

    return removedAnElement
  }

  override fun iterator(): MutableIterator<T> = Iterator()
}
