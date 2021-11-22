package com.raycenity.misc.observer

import com.raycenity.misc.RuntimeImmutableList

class ObservableList<T> private constructor(private val backing: MutableList<T>) : List<T> by backing,
    MutableList<T> {
    private inner class Iterator(startIndex: Int = 0) : MutableListIterator<T> {

        private val backingIterator: MutableListIterator<T> = backing.listIterator(startIndex)
        private var last: IndexedValue<T>? = null

        override fun hasNext(): Boolean = backingIterator.hasNext()
        override fun next(): T {
            val index = backingIterator.nextIndex()
            val element = backingIterator.next()
            last = IndexedValue(index, element)
            return element
        }

        override fun remove() {
            backingIterator.remove()
            _didRemove.publish(last!!)
        }

        override fun hasPrevious(): Boolean = backingIterator.hasPrevious()
        override fun nextIndex(): Int = backingIterator.nextIndex()
        override fun previous(): T {
            val index = backingIterator.previousIndex()
            val element = backingIterator.previous()
            last = IndexedValue(index, element)
            return element
        }

        override fun previousIndex(): Int = backingIterator.previousIndex()
        override fun add(element: T): Unit = backingIterator.add(element).also {
            _didAdd.publish(IndexedValue(previousIndex(), element))
        }

        override fun set(element: T) {
            backingIterator.set(element)
            _didRemove.publish(last!!)
            _didAdd.publish(IndexedValue(last!!.index, element))
        }
    }

    private val _didAdd: Publisher<IndexedValue<T>> = Publisher()
    val didAdd: Observable<IndexedValue<T>>
        get() = _didAdd

    private val _didRemove: Publisher<IndexedValue<T>> = Publisher()
    val didRemove: Observable<IndexedValue<T>>
        get() = _didRemove

    constructor(initial: Iterable<T>) : this(initial.toMutableList())

    override operator fun set(index: Int, element: T): T = backing.set(index, element).also {
        _didRemove.publish(IndexedValue(index, it))
        _didAdd.publish(IndexedValue(index, element))
    }

    override fun add(element: T): Boolean = backing.add(element).also {
        _didAdd.publish(IndexedValue(lastIndex, element))
    }

    override fun add(index: Int, element: T) {
        backing.add(index, element)
        _didAdd.publish(IndexedValue(index, element))
    }

    override fun addAll(elements: Collection<T>): Boolean = backing.addAll(elements).also {
        val initialIndex = size - elements.size
        for ((indexOffset, element) in elements.withIndex()) {
            val index = initialIndex + indexOffset
            _didAdd.publish(IndexedValue(index, element))
        }
    }

    override fun addAll(index: Int, elements: Collection<T>): Boolean =
        backing.addAll(index, elements).also {
            for ((indexOffset, element) in elements.withIndex()) {
                val elementIndex = index + indexOffset
                _didAdd.publish(IndexedValue(elementIndex, element))
            }
        }

    override fun clear() {
        val oldBacking = backing.toList()
        backing.clear()
        for ((index, element) in oldBacking.withIndex().reversed()) {
            _didRemove.publish(IndexedValue(index, element))
        }
    }

    override fun remove(element: T): Boolean = when (val elementIndex = indexOf(element)) {
        -1 -> false
        else -> {
            removeAt(elementIndex)
            true
        }
    }

    override fun removeAt(index: Int): T = backing.removeAt(index).also {
        _didRemove.publish(IndexedValue(index, it))
    }

    override fun removeAll(elements: Collection<T>): Boolean {
        val oldBacking = backing.toList()
        val removedAnElement = backing.retainAll(elements)

        for ((index, element) in oldBacking.withIndex().reversed()) {
            if (elements.contains(element)) {
                _didRemove.publish(IndexedValue(index, element))
            }
        }

        return removedAnElement
    }

    override fun retainAll(elements: Collection<T>): Boolean {
        val oldBacking = backing.toList()
        val removedAnElement = backing.retainAll(elements)

        for ((index, element) in oldBacking.withIndex().reversed()) {
            if (!elements.contains(element)) {
                _didRemove.publish(IndexedValue(index, element))
            }
        }

        return removedAnElement
    }

    override fun iterator(): MutableIterator<T> = Iterator()
    override fun listIterator(): MutableListIterator<T> = Iterator()
    override fun listIterator(index: Int): MutableListIterator<T> = Iterator(index)
    override fun subList(fromIndex: Int, toIndex: Int): MutableList<T> =
        RuntimeImmutableList(backing.subList(fromIndex, toIndex))
}
