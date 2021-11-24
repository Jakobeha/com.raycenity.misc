package com.raycenity.misc.functional

/** ɑ → ɑ */
@FunctionalInterface
interface IdFunction {
    companion object {
        /** Unfortunately Kotlin doesn't do SAM constructors with this */
        operator fun invoke(function: (Any?) -> Any?): IdFunction = object : IdFunction {
            @Suppress("UNCHECKED_CAST")
            override fun <T> invoke(argument: T): T = function(argument) as T
        }
    }

    operator fun <T> invoke(argument: T): T
}
