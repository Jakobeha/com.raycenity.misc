@file:Suppress("LocalVariableName", "DuplicatedCode")

package com.raycenity.misc.extensions

/** This is not my code - from https://stackoverflow.com/questions/3537706/how-to-unescape-a-java-string-literal-in-java */
/*
 *
 * unescape_perl_string()
 *
 *      Tom Christiansen <tchrist@perl.com>
 *      Sun Nov 28 12:55:24 MST 2010
 *
 * It's completely ridiculous that there's no standard
 * unescape_java_string function.  Since I have to do the
 * damn thing myself, I might as well make it halfway useful
 * by supporting things Java was too stupid to consider in
 * strings:
 *
 *   => "?" items  are additions to Java string escapes
 *                 but normal in Java regexes
 *
 *   => "!" items  are also additions to Java regex escapes
 *
 * Standard singletons: ?\a ?\e \f \n \r \t
 *
 *      NB: \b is unsupported as backspace so it can pass-through
 *          to the regex translator untouched; I refuse to make anyone
 *          doublebackslash it as doublebackslashing is a Java idiocy
 *          I desperately wish would die out.  There are plenty of
 *          other ways to write it:
 *
 *              \cH, \12, \012, \x08 \x{8}, \u0008, \U00000008
 *
 * Octal escapes: \0 \0N \0NN \N \NN \NNN
 *    Can range up to !\777 not \377
 *
 *      NOTFORME: add !\o{NNNNN}
 *          last Unicode is 4177777
 *          maxint is 37777777777
 *
 * Control chars: ?\cX
 *      Means: ord(X) ^ ord('@')
 *
 * Old hex escapes: \xXX
 *      unbraced must be 2 xdigits
 *
 * Perl hex escapes: !\x{XXX} braced may be 1-8 xdigits
 *       NB: proper Unicode never needs more than 6, as highest
 *           valid codepoint is 0x10FFFF, not maxint 0xFFFFFFFF
 *
 * Lame Java escape: \[IDIOT JAVA PREPROCESSOR]uXXXX must be
 *                   exactly 4 xdigits;
 *
 *       I can't write XXXX in this comment where it belongs
 *       because the damned Java Preprocessor can't mind its
 *       own business.  Idiots!
 *
 * Lame Python escape: !\UXXXXXXXX must be exactly 8 xdigits
 *
 * NOTFORME: Perl translation escapes: \Q \U \L \E \[IDIOT JAVA PREPROCESSOR]u \l
 *       These are not so important to cover if you're passing the
 *       result to Pattern.compile(), since it handles them for you
 *       further downstream.  Hm, what about \[IDIOT JAVA PREPROCESSOR]u?
 *
 */
val String.unescape: String
    get() {
    /*
     * In contrast to fixing Java's broken regex charclasses,
     * this one need be no bigger, as unescaping shrinks the string
     * here, where in the other one, it grows it.
     */
    val newstr = StringBuffer(length)
    var saw_backslash = false
    var i = 0
    while (i < length) {
        var cp = codePointAt(i)
        if (codePointAt(i) > Character.MAX_VALUE.code) {
            i++
            /****WE HATES UTF-16! WE HATES IT FOREVERSES!!! */
        }
        if (!saw_backslash) {
            if (cp == '\\'.code) {
                saw_backslash = true
            } else {
                newstr.append(Character.toChars(cp))
            }
            i++
            continue  /* switch */
        }
        when (cp.toChar()) {
            '\\' -> newstr.append('\\')
            '\'' -> newstr.append('\'')
            '"' -> newstr.append('"')
            'r' -> newstr.append('\r')
            'n' -> newstr.append('\n')
            'b' -> newstr.append('\b')
            't' -> newstr.append('\t')
            'a' -> newstr.append('\u0007')
            'e' -> newstr.append('\u001b')
            'c' -> {
                if (++i == length) {
                    die("trailing \\c")
                }
                cp = codePointAt(i)
                /*
                 * don't need to grok surrogates, as next line blows them up
                 */if (cp > 0x7f) {
                    die("expected ASCII after \\c")
                }
                newstr.append(Character.toChars(cp xor 64))
            }
            '8', '9' -> {
                die("illegal octal digit")
                --i
                run {
                    if (i + 1 == length) {
                        /* found \0 at end of string */
                        newstr.append(Character.toChars(0))
                        return@run /* switch */
                    }
                    i++
                    var digits = 0
                    var j = 0
                    while (j <= 2) {
                        if (i + j == length) {
                            break /* for */
                        }
                        /* safe because will unread surrogate */
                        val ch = this[i + j].code
                        if (ch < '0'.code || ch > '7'.code) {
                            break /* for */
                        }
                        digits++
                        j++
                    }
                    if (digits == 0) {
                        --i
                        newstr.append('\u0000')
                        return@run /* switch */
                    }
                    var value = 0
                    try {
                        value =
                            substring(i, i + digits).toInt(8)
                    } catch (nfe: NumberFormatException) {
                        die("invalid octal value for \\0 escape")
                    }
                    newstr.append(Character.toChars(value))
                    i += digits - 1
                }
            }
            '1', '2', '3', '4', '5', '6', '7' -> {
                --i
                run {
                    if (i + 1 == length) {
                        newstr.append(Character.toChars(0))
                        return@run
                    }
                    i++
                    var digits = 0
                    var j = 0
                    while (j <= 2) {
                        if (i + j == length) {
                            break
                        }
                        val ch = this[i + j].code
                        if (ch < '0'.code || ch > '7'.code) {
                            break
                        }
                        digits++
                        j++
                    }
                    if (digits == 0) {
                        --i
                        newstr.append('\u0000')
                        return@run
                    }
                    var value = 0
                    try {
                        value =
                            substring(i, i + digits).toInt(8)
                    } catch (nfe: NumberFormatException) {
                        die("invalid octal value for \\0 escape")
                    }
                    newstr.append(Character.toChars(value))
                    i += digits - 1
                }
            }
            '0' -> {
                if (i + 1 == length) {
                    newstr.append(Character.toChars(0))
                    break
                }
                i++
                var digits = 0
                var j = 0
                while (j <= 2) {
                    if (i + j == length) {
                        break
                    }
                    val ch = this[i + j].code
                    if (ch < '0'.code || ch > '7'.code) {
                        break
                    }
                    digits++
                    j++
                }
                if (digits == 0) {
                    --i
                    newstr.append('\u0000')
                    break
                }
                var value = 0
                try {
                    value =
                        substring(i, i + digits).toInt(8)
                } catch (nfe: NumberFormatException) {
                    die("invalid octal value for \\0 escape")
                }
                newstr.append(Character.toChars(value))
                i += digits - 1
            } /* end case '0' */
            'x' -> {
                if (i + 2 > length) {
                    die("string too short for \\x escape")
                }
                i++
                var saw_brace = false
                if (this[i] == '{') {
                    /* ^^^^^^ ok to ignore surrogates here */
                    i++
                    saw_brace = true
                }
                var j = 0
                while (j < 8) {
                    if (!saw_brace && j == 2) {
                        break /* for */
                    }

                    /*
                     * ASCII test also catches surrogates
                     */
                    val ch = this[i + j].code
                    if (ch > 127) {
                        die("illegal non-ASCII hex digit in \\x escape")
                    }
                    if (saw_brace && ch == '}'.code) {
                        break /* for */
                    }
                    if (!(ch >= '0'.code && ch <= '9'.code
                            ||
                            ch >= 'a'.code && ch <= 'f'.code
                            ||
                            ch >= 'A'.code && ch <= 'F'.code)
                    ) {
                        die(
                            String.format(
                                "illegal hex digit #%d '%c' in \\x", ch, ch
                            )
                        )
                    }
                    j++
                }
                if (j == 0) {
                    die("empty braces in \\x{} escape")
                }
                var value = 0
                try {
                    value = substring(i, i + j).toInt(16)
                } catch (nfe: NumberFormatException) {
                    die("invalid hex value for \\x escape")
                }
                newstr.append(Character.toChars(value))
                if (saw_brace) {
                    j++
                }
                i += j - 1
            }
            'u' -> {
                if (i + 4 > length) {
                    die("string too short for \\u escape")
                }
                i++
                val isBracketed = this[i] == '{'
                if (isBracketed) {
                    i++
                }
                var j = 0
                while (j < 4) {
                    /* this also handles the surrogate issue */
                    if (this[i + j].code > 127) {
                        die("illegal non-ASCII hex digit in \\u escape")
                    }
                    j++
                }
                var value = 0
                try {
                    value = substring(i, i + j).toInt(16)
                } catch (nfe: NumberFormatException) {
                    die("invalid hex value for \\u escape")
                }
                newstr.append(Character.toChars(value))
                i += j - 1
                if (isBracketed) {
                    i++
                    if (this[i] != '}') {
                        die("missing closing bracket, got ${this[i]}")
                    }
                }
            }
            'U' -> {
                if (i + 8 > length) {
                    die("string too short for \\U escape")
                }
                i++
                val isBracketed = this[i] == '{'
                if (isBracketed) {
                    i++
                }
                var j = 0
                while (j < 8) {

                    /* this also handles the surrogate issue */if (this[i + j].code > 127) {
                        die("illegal non-ASCII hex digit in \\U escape")
                    }
                    j++
                }
                var value = 0
                try {
                    value = substring(i, i + j).toInt(16)
                } catch (nfe: NumberFormatException) {
                    die("invalid hex value for \\U escape")
                }
                newstr.append(Character.toChars(value))
                i += j - 1
                if (isBracketed) {
                    i++
                    if (this[i] != '}') {
                        die("missing closing bracket, got ${this[i]}")
                    }
                }
            }
            else -> {
                newstr.append('\\')
                newstr.append(Character.toChars(cp))
            }
        }
        saw_backslash = false
        i++
    }

    /* weird to leave one at the end */if (saw_backslash) {
        newstr.append('\\')
    }
    return newstr.toString()
}

private fun die(foa: String) {
    throw IllegalArgumentException(foa)
}
