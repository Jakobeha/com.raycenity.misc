package com.raycenity.misc.enm

/**
 * Map internally backed by an array. Not partial AKA all keys have values unless Value is optional.
 */
sealed class EnumMap<Key : Enum<Key>, out Value> : Map<Key, Value> {
  companion object {
    inline fun <reified Key : Enum<Key>, reified Value> new(): EnumMap<Key, Value?> =
      Mutable.new()

    inline fun <reified Key : Enum<Key>, reified Value> new(getValue: (Key) -> Value): EnumMap<Key, Value> =
      Mutable.new(getValue)

    inline fun <reified Key : Enum<Key>, reified Value> new(
      map: Map<Key, Value>,
      defaultGetter: (Key) -> Value = { key ->
        throw IllegalArgumentException("map converted to enum map is missing a key: $key")
      }
    ): EnumMap<Key, Value> =
      Mutable.new(map, defaultGetter)

    inline operator fun <Key : Enum<Key>, reified Value> invoke(
      enumValues: Array<Key>,
      getValue: (Key) -> Value
    ): EnumMap<Key, Value> =
      @Suppress("DEPRECATION")
      Mutable(
        Array(enumValues.size) { getValue(enumValues[it]) },
        enumValues
      )
  }

  abstract override operator fun get(key: Key): Value

  object CantRemoveEnumMapEntriesException :
    UnsupportedOperationException("can't remove enum map entries")

  class Mutable<Key : Enum<Key>, Value> @Deprecated(
    "usually you don't want this",
    ReplaceWith("EnumMap.Mutable.new")
  ) constructor(
    private val backing: Array<Value>,
    private val enumValues: Array<Key>
  ) : EnumMap<Key, Value>(),
    MutableMap<Key, Value> {
    companion object {
      inline fun <reified Key : Enum<Key>, reified Value> new(): Mutable<Key, Value?> =
        new { null }

      @Suppress("DEPRECATION")
      inline fun <reified Key : Enum<Key>, reified Value> new(getValue: (Key) -> Value): Mutable<Key, Value> =
        Mutable(
          Array(enumValues<Key>().size) { getValue(enumValues<Key>()[it]) },
          enumValues()
        )

      inline fun <reified Key : Enum<Key>, reified Value> new(
        map: Map<Key, Value>,
        defaultGetter: (Key) -> Value = { key ->
          throw IllegalArgumentException("map converted to enum map is missing a key: $key")
        }
      ): Mutable<Key, Value> =
        new { key -> map[key] ?: defaultGetter(key) }
    }

    private inner class Entry(private val index: Int) :
      MutableMap.MutableEntry<Key, Value> {
      override val key: Key = enumValues[index]
      override var value: Value = backing[index]

      override fun setValue(newValue: Value): Value {
        val oldValue = value
        backing[index] = newValue
        value = newValue
        return oldValue
      }
    }

    private inner class Entries :
      MutableSet<MutableMap.MutableEntry<Key, Value>> {
      private inner class Iterator : MutableIterator<Entry> {
        private var index: Int = 0

        override fun hasNext(): Boolean = index < backing.size

        override fun next(): Entry = Entry(index++)

        override fun remove() {
          throw CantRemoveEnumMapEntriesException
        }
      }

      override fun add(element: MutableMap.MutableEntry<Key, Value>): Boolean {
        // Always a replace
        return if (this@Mutable[element.key] == element.value) {
          false
        } else {
          this@Mutable[element.key] = element.value
          true
        }
      }

      override fun addAll(elements: Collection<MutableMap.MutableEntry<Key, Value>>): Boolean {
        var added = false
        for (element in elements) {
          added = add(element) || added
        }
        return added
      }

      override fun clear() {
        throw CantRemoveEnumMapEntriesException
      }

      override fun iterator(): MutableIterator<Entry> = Iterator()

      override fun remove(element: MutableMap.MutableEntry<Key, Value>): Boolean {
        throw CantRemoveEnumMapEntriesException
      }

      override fun removeAll(elements: Collection<MutableMap.MutableEntry<Key, Value>>): Boolean {
        throw CantRemoveEnumMapEntriesException
      }

      override fun retainAll(elements: Collection<MutableMap.MutableEntry<Key, Value>>): Boolean {
        throw CantRemoveEnumMapEntriesException
      }

      override val size: Int
        get() = backing.size

      override fun contains(element: MutableMap.MutableEntry<Key, Value>): Boolean =
        this@Mutable[element.key] == element.value

      override fun containsAll(elements: Collection<MutableMap.MutableEntry<Key, Value>>): Boolean =
        elements.all { contains(it) }

      override fun isEmpty(): Boolean = false
    }

    private inner class Keys : MutableSet<Key> {
      private inner class Iterator : MutableIterator<Key> {
        private var index: Int = 0

        override fun hasNext(): Boolean = index < backing.size

        override fun next(): Key = enumValues[index++]

        override fun remove() {
          throw CantRemoveEnumMapEntriesException
        }
      }

      override fun add(element: Key): Boolean {
        return false
      }

      override fun addAll(elements: Collection<Key>): Boolean {
        return false
      }

      override fun clear() {
        throw CantRemoveEnumMapEntriesException
      }

      override fun iterator(): MutableIterator<Key> = Iterator()

      override fun remove(element: Key): Boolean {
        throw CantRemoveEnumMapEntriesException
      }

      override fun removeAll(elements: Collection<Key>): Boolean {
        throw CantRemoveEnumMapEntriesException
      }

      override fun retainAll(elements: Collection<Key>): Boolean {
        throw CantRemoveEnumMapEntriesException
      }

      override val size: Int
        get() = backing.size

      override fun contains(element: Key): Boolean = true

      override fun containsAll(elements: Collection<Key>): Boolean = true

      override fun isEmpty(): Boolean = false
    }

    private inner class Values : MutableSet<Value> {
      private inner class Iterator : MutableIterator<Value> {
        private var index: Int = 0

        override fun hasNext(): Boolean = index < backing.size

        override fun next(): Value = backing[index++]

        override fun remove() {
          throw CantRemoveEnumMapEntriesException
        }
      }

      override fun add(element: Value): Boolean {
        throw UnsupportedOperationException("can't add value to enum map because we don't know the key")
      }

      override fun addAll(elements: Collection<Value>): Boolean {
        throw UnsupportedOperationException("can't add values to enum map because we don't know the keys")
      }

      override fun clear() {
        throw CantRemoveEnumMapEntriesException
      }

      override fun iterator(): MutableIterator<Value> = Iterator()

      override fun remove(element: Value): Boolean {
        throw CantRemoveEnumMapEntriesException
      }

      override fun removeAll(elements: Collection<Value>): Boolean {
        throw CantRemoveEnumMapEntriesException
      }

      override fun retainAll(elements: Collection<Value>): Boolean {
        throw CantRemoveEnumMapEntriesException
      }

      override val size: Int
        get() = backing.size

      override fun contains(element: Value): Boolean =
        this@Mutable.containsValue(element)

      override fun containsAll(elements: Collection<Value>): Boolean =
        elements.all { contains(it) }

      override fun isEmpty(): Boolean = false
    }

    override val size: Int
      get() = backing.size

    override fun containsKey(key: Key): Boolean = true

    override fun containsValue(value: Value): Boolean = backing.contains(value)

    override fun get(key: Key): Value = backing[key.ordinal]

    override fun isEmpty(): Boolean = false

    override val entries: MutableSet<MutableMap.MutableEntry<Key, Value>>
      get() = Entries()
    override val keys: MutableSet<Key>
      get() = Keys()
    override val values: MutableCollection<Value>
      get() = Values()

    override fun clear() {
      throw CantRemoveEnumMapEntriesException
    }

    override fun put(key: Key, value: Value): Value {
      val oldValue = backing[key.ordinal]
      backing[key.ordinal] = value
      return oldValue
    }

    override fun putAll(from: Map<out Key, Value>) {
      for (entry in from) {
        put(entry.key, entry.value)
      }
    }

    override fun remove(key: Key): Value? {
      throw CantRemoveEnumMapEntriesException
    }
  }

  class Computed<Key : Enum<Key>, Value> @Deprecated(
    "usually you don't want this",
    ReplaceWith("EnumMap.Computed.new")
  ) constructor(
    private val get: (Key) -> Value,
    private val set: (Key, Value) -> Unit,
    private val enumValues: Array<Key>
  ) : EnumMap<Key, Value>(), MutableMap<Key, Value> {
    companion object {
      @Suppress("DEPRECATION")
      inline fun <reified Key : Enum<Key>, Value> new(
        noinline get: (Key) -> Value,
        noinline set: (Key, Value) -> Unit
      ): Computed<Key, Value> = Computed(get, set, enumValues())
    }

    private inner class Entry(index: Int) :
      MutableMap.MutableEntry<Key, Value> {
      override val key: Key = enumValues[index]
      override var value: Value = get(key)

      override fun setValue(newValue: Value): Value {
        val oldValue = value
        set(key, newValue)
        value = newValue
        return oldValue
      }
    }

    private inner class Entries :
      MutableSet<MutableMap.MutableEntry<Key, Value>> {
      private inner class Iterator : MutableIterator<Entry> {
        private var index: Int = 0

        override fun hasNext(): Boolean = index < enumValues.size

        override fun next(): Entry = Entry(index++)

        override fun remove() {
          throw CantRemoveEnumMapEntriesException
        }
      }

      override fun add(element: MutableMap.MutableEntry<Key, Value>): Boolean {
        // Always a replace
        return if (get(element.key) == element.value) {
          false
        } else {
          set(element.key, element.value)
          true
        }
      }

      override fun addAll(elements: Collection<MutableMap.MutableEntry<Key, Value>>): Boolean {
        var added = false
        for (element in elements) {
          added = add(element) || added
        }
        return added
      }

      override fun clear() {
        throw CantRemoveEnumMapEntriesException
      }

      override fun iterator(): MutableIterator<Entry> = Iterator()

      override fun remove(element: MutableMap.MutableEntry<Key, Value>): Boolean {
        throw CantRemoveEnumMapEntriesException
      }

      override fun removeAll(elements: Collection<MutableMap.MutableEntry<Key, Value>>): Boolean {
        throw CantRemoveEnumMapEntriesException
      }

      override fun retainAll(elements: Collection<MutableMap.MutableEntry<Key, Value>>): Boolean {
        throw CantRemoveEnumMapEntriesException
      }

      override val size: Int
        get() = enumValues.size

      override fun contains(element: MutableMap.MutableEntry<Key, Value>): Boolean =
        get(element.key) == element.value

      override fun containsAll(elements: Collection<MutableMap.MutableEntry<Key, Value>>): Boolean =
        elements.all { contains(it) }

      override fun isEmpty(): Boolean = false
    }

    private inner class Keys : MutableSet<Key> {
      private inner class Iterator : MutableIterator<Key> {
        private var index: Int = 0

        override fun hasNext(): Boolean = index < enumValues.size

        override fun next(): Key = enumValues[index++]

        override fun remove() {
          throw CantRemoveEnumMapEntriesException
        }
      }

      override fun add(element: Key): Boolean {
        return false
      }

      override fun addAll(elements: Collection<Key>): Boolean {
        return false
      }

      override fun clear() {
        throw CantRemoveEnumMapEntriesException
      }

      override fun iterator(): MutableIterator<Key> = Iterator()

      override fun remove(element: Key): Boolean {
        throw CantRemoveEnumMapEntriesException
      }

      override fun removeAll(elements: Collection<Key>): Boolean {
        throw CantRemoveEnumMapEntriesException
      }

      override fun retainAll(elements: Collection<Key>): Boolean {
        throw CantRemoveEnumMapEntriesException
      }

      override val size: Int
        get() = enumValues.size

      override fun contains(element: Key): Boolean = true

      override fun containsAll(elements: Collection<Key>): Boolean = true

      override fun isEmpty(): Boolean = false
    }

    private inner class Values : MutableSet<Value> {
      private inner class Iterator : MutableIterator<Value> {
        private var index: Int = 0

        override fun hasNext(): Boolean = index < enumValues.size

        override fun next(): Value = get(enumValues[index++])

        override fun remove() {
          throw CantRemoveEnumMapEntriesException
        }
      }

      override fun add(element: Value): Boolean {
        throw UnsupportedOperationException("can't add value to enum map because we don't know the key")
      }

      override fun addAll(elements: Collection<Value>): Boolean {
        throw UnsupportedOperationException("can't add values to enum map because we don't know the keys")
      }

      override fun clear() {
        throw CantRemoveEnumMapEntriesException
      }

      override fun iterator(): MutableIterator<Value> = Iterator()

      override fun remove(element: Value): Boolean {
        throw CantRemoveEnumMapEntriesException
      }

      override fun removeAll(elements: Collection<Value>): Boolean {
        throw CantRemoveEnumMapEntriesException
      }

      override fun retainAll(elements: Collection<Value>): Boolean {
        throw CantRemoveEnumMapEntriesException
      }

      override val size: Int
        get() = enumValues.size

      override fun contains(element: Value): Boolean =
        containsValue(element)

      override fun containsAll(elements: Collection<Value>): Boolean =
        elements.all { contains(it) }

      override fun isEmpty(): Boolean = false
    }

    override val size: Int
      get() = enumValues.size

    override fun containsKey(key: Key): Boolean = true

    override fun containsValue(value: Value): Boolean =
      enumValues.any { get(it) == value }

    override fun get(key: Key): Value = get.invoke(key)

    override fun isEmpty(): Boolean = false

    override val entries: MutableSet<MutableMap.MutableEntry<Key, Value>>
      get() = Entries()
    override val keys: MutableSet<Key>
      get() = Keys()
    override val values: MutableCollection<Value>
      get() = Values()

    override fun clear() {
      throw CantRemoveEnumMapEntriesException
    }

    override fun put(key: Key, value: Value): Value {
      val oldValue = get(key)
      set(key, value)
      return oldValue
    }

    override fun putAll(from: Map<out Key, Value>) {
      for (entry in from) {
        put(entry.key, entry.value)
      }
    }

    override fun remove(key: Key): Value? {
      throw CantRemoveEnumMapEntriesException
    }
  }
}
