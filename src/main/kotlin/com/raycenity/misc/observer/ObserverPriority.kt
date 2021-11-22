package com.raycenity.misc.observer

data class ObserverPriority(val priority: Float) : Comparable<ObserverPriority> {
    companion object {
        val DEFAULT: ObserverPriority = ObserverPriority(0.0f)
    }

    override fun compareTo(other: ObserverPriority): Int = priority compareTo other.priority
}
