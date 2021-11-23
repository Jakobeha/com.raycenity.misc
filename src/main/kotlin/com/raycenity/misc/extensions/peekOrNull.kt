package com.raycenity.misc.extensions

import java.util.Stack

val <T> Stack<T>.peekOrNull: T?
    get() = when (isEmpty()) {
        true -> null
        false -> peek()
    }
