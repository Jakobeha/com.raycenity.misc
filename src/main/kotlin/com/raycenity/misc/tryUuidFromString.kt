package com.raycenity.misc

import java.util.UUID

fun tryUuidFromString(encoded: String): UUID? = try {
  UUID.fromString(encoded)
} catch (exception: IllegalArgumentException) {
  null
}
