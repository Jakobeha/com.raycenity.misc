package com.raycenity.misc.extensions

/* Code from https://stackoverflow.com/questions/2406121/how-do-i-escape-a-string-in-java */
val Char.escape: String
    get() = when (this) {
        '\\' -> "\\\\"
        '\t' -> "\\t"
        '\b' -> "\\b"
        '\n' -> "\\n"
        '\r' -> "\\r"
        '\'' -> "\\'"
        else -> toString()
    }

val String.escape: String
    get() = replace("\\", "\\\\")
        .replace("\t", "\\t")
        .replace("\b", "\\b")
        .replace("\n", "\\n")
        .replace("\r", "\\r")
        .replace("\"", "\\\"")
        .replace(Regex("[^\\u0000-\\u007F]+")) { "\\u{${it.value.codePointAt(0).toString(16).padStart(4, '0')}}" }

val String.escapeTripleQuote: String
    get() = replace("\\", "\\\\")
        .replace("\"\"\"", "\\\"\"\"")
        .dropLastWhile { it == '"' } +
        takeLastWhile { it == '"' }.replace("\"", "\\\"")
