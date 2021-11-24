package com.raycenity.misc.extensions

fun <A, B> Collection<A>.zipMax(other: Collection<B>): Iterable<Pair<A?, B?>> =
    padded(other.size).zip(other.padded(size))

fun <T> Collection<T>.padded(minSize: Int): MutableList<T?> = toMutableList<T?>().apply {
    while (size < minSize) {
        add(null)
    }
}
