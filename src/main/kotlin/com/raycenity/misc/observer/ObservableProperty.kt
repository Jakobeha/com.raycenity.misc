package com.raycenity.misc.observer

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class ObservableProperty<P>(private val didChangeValue: Publisher<P>, initialValue: P) : ReadWriteProperty<Any, P> {
    private var field: P = initialValue

    override operator fun getValue(thisRef: Any, property: KProperty<*>): P = field

    override operator fun setValue(thisRef: Any, property: KProperty<*>, value: P) {
        val oldValue = field
        field = value
        didChangeValue.publish(oldValue)
    }
}
