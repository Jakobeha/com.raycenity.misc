package com.raycenity.misc.extensions

val String.lineStartIndices: Sequence<Int>
    get() = lineSequence().runningFold(0) { len, string -> len + string.length + 1 }

fun String.lineOfIndex(offset: Int): Int =
    lineStartIndices.count { it <= offset }
