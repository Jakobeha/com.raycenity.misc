package com.raycenity.misc.extensions

fun IntRange.clamp(other: IntRange): IntRange = IntRange(maxOf(first, other.first), minOf(last, other.last))
