package com.raycenity.misc.extensions

val String.snakeCase: String
    get() {
        val result = StringBuilder()
        for (char in this) {
            when {
                !char.isLetterOrDigit() || (result.isEmpty() && char.isDigit()) -> throw IllegalArgumentException("$this is not a valid identifier so it can't be converted into snake case")
                char.isUpperCase() && result.isNotEmpty() -> result.append('_')
            }
            result.append(char.lowercase())
        }
        return result.toString()
    }
