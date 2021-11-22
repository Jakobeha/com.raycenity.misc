package com.raycenity.misc

class RuntimeImmutableList<T>(private val backing: List<T>) : List<T> by backing, MutableList<T> {
    object Exception :
        UnsupportedOperationException("this list is actually immutable even though its type says it's mutable")

    class Iterator<T>(private val backing: ListIterator<T>) : ListIterator<T> by backing, MutableListIterator<T> {
        override fun add(element: T) = throw Exception
        override fun remove() = throw Exception
        override fun set(element: T) = throw Exception
    }

    override fun add(element: T): Boolean = throw Exception
    override fun add(index: Int, element: T) = throw Exception
    override fun addAll(index: Int, elements: Collection<T>) = throw Exception
    override fun addAll(elements: Collection<T>) = throw Exception
    override fun clear() = throw Exception
    override fun remove(element: T) = throw Exception
    override fun removeAll(elements: Collection<T>) = throw Exception
    override fun removeAt(index: Int): T = throw Exception
    override fun retainAll(elements: Collection<T>) = throw Exception
    override fun set(index: Int, element: T) = throw Exception
    override fun iterator(): MutableIterator<T> = Iterator(backing.listIterator())
    override fun listIterator(): MutableListIterator<T> = Iterator(backing.listIterator())
    override fun listIterator(index: Int): MutableListIterator<T> = Iterator(backing.listIterator(index))
    override fun subList(fromIndex: Int, toIndex: Int): MutableList<T> =
        RuntimeImmutableList(backing.subList(fromIndex, toIndex))
}
