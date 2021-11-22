package com.raycenity.misc

import java.util.*

/**
 * A continuation from Category Theory (I think).
 * See https://www.haskellforall.com/2012/12/the-continuation-monad.html
 * for an explanation of what continuations are.
 */
@FunctionalInterface
interface Continuation<out Input> {
  companion object {
    operator fun <Input> invoke(
      body: suspend (suspend (Input) -> Unit) -> Unit
    ): Continuation<Input> = object : Continuation<Input> {
      override suspend fun <Result> invoke(fn: suspend (Input) -> Result): Result {
        var result: Optional<Result> = Optional.empty()
        body { input ->
          result = Optional.ofNullable(fn(input))
        }
        return result.orElse(null)
      }
    }
  }

  suspend operator fun <Result> invoke(fn: suspend (Input) -> Result): Result
}
