package com.raycenity.misc.enm

sealed class EnumSet<T : Enum<T>> : Set<T> {
  companion object {
    inline fun <reified T : Enum<T>> new(): EnumSet<T> = Mutable.new()

    inline fun <reified T : Enum<T>> all(): EnumSet<T> = Mutable.all()

    inline fun <reified T : Enum<T>> new(vararg elements: T): EnumSet<T> =
      Mutable.new(elements.asIterable())

    inline fun <reified T : Enum<T>> new(elements: Iterable<T>): EnumSet<T> =
      Mutable.new(elements)

    inline fun <reified T : Enum<T>> new(predicate: (T) -> Boolean): EnumSet<T> =
      Mutable.new(predicate)
  }

  val stringSet: Set<String>
    get() = map { it.name }.toSet()

  class Mutable<T : Enum<T>>(private val backing: java.util.EnumSet<T>) :
    EnumSet<T>(),
    MutableSet<T> by backing {
    companion object {
      inline fun <reified T : Enum<T>> new(): Mutable<T> {
        val backing = java.util.EnumSet.noneOf(T::class.java)
        return Mutable(backing)
      }

      inline fun <reified T : Enum<T>> all(): Mutable<T> {
        val backing = java.util.EnumSet.allOf(T::class.java)
        return Mutable(backing)
      }

      inline fun <reified T : Enum<T>> new(vararg elements: T): Mutable<T> =
        new(elements.asIterable())

      inline fun <reified T : Enum<T>> new(elements: Iterable<T>): Mutable<T> {
        val result = new<T>()
        result.addAll(elements)
        return result
      }

      inline fun <reified T : Enum<T>> new(predicate: (T) -> Boolean): Mutable<T> {
        val result = new<T>()
        for (element in enumValues<T>()) {
          if (predicate(element)) {
            result.add(element)
          }
        }
        return result
      }
    }
  }
}
