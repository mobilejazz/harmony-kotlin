package com.mobilejazz.harmony.kotlin.core.ext

import com.harmony.kotlin.ext.Dates
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class DatesTests {

  @Test
  fun nowFunction_shouldBeDifferent_whenIsCalledTwice() {
    val time = Dates.now().time
    Thread.sleep(1000)
    val time2 = Dates.now().time

    assertThat(time).isNotEqualTo(time2)
  }
}
