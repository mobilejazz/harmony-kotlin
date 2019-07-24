package com.mobilejazz.harmony.kotlin.core.threading.extensions

import org.junit.Assert
import org.junit.Test


class ListenableFutureExtTest {

  // region blockError tests
  @Test
  fun shouldReturnUnit_WhenInvokingBlockError_GivingAFailingFuture() {
    // Given
    val failingFuture = Future<Boolean> {
      throw Exception()
    }

    // When
    val unit = failingFuture.blockError<Throwable>().get()

    // Then
    Assert.assertEquals(Unit, unit)
  }
  // endregion
}