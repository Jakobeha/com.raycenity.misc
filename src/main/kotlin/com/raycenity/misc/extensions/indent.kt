package com.raycenity.misc.extensions

fun String.indent(indent: String, indentFirst: Boolean = false, indentBlank: Boolean = false): String = StringBuilder().apply {
    val lines = lines()
    for ((index, line) in lines.withIndex()) {
        if ((indentFirst || index == 0) && (indentBlank || line.isNotBlank())) {
            append(indent)
        }
        append(line)
        if (index != lines.lastIndex || endsWith('\n')) {
            append('\n')
        }
    }
}.toString()
