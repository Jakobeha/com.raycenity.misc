package com.raycenity.misc

class AddOnlyList<T>(private val wrapped: MutableList<T>): List<T> by wrapped {
  fun add(element: T) {
    wrapped.add(element)
  }
}
