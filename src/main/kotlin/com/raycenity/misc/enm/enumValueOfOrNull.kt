package com.raycenity.misc.enm

inline fun <reified MyEnum : Enum<MyEnum>> enumValueOfOrNull(
  input: String
): MyEnum? = try {
  enumValueOf<MyEnum>(input)
} catch (exception: IllegalArgumentException) {
  null
}

inline fun <reified MyEnum : Enum<MyEnum>> enumListOfOrNull(
  inputs: List<String>
): List<MyEnum>? = try {
  inputs.map { input -> enumValueOf(input) }
} catch (exception: IllegalArgumentException) {
  null
}
