package com.harmony.kotlin.common.presenter

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.testing.TestLifecycleOwner
import com.harmony.kotlin.CoroutinesTestRule
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertNotNull

class PresenterViewHolderTest {

  @get:Rule
  val coroutinesTestRule = CoroutinesTestRule()

  @Test
  fun `assert view reference is cleared when lifecycle reaches destroyed state`() = runTest {
    val lifecycleOwner = TestLifecycleOwner()
    val viewHolder = PresenterViewHolder(lifecycleOwner)

    lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)

    assertNull(viewHolder.get())
  }

  @Test
  fun `assert view is not null when lifecycle reaches any state except destroyed`() = runTest {
    val lifecycleOwner = TestLifecycleOwner()
    val viewHolder = PresenterViewHolder(lifecycleOwner)
    val nonDestroyedState = Lifecycle.Event.values()
      .toMutableList()
      .minus(listOf(Lifecycle.Event.ON_DESTROY, Lifecycle.Event.ON_ANY))
      .random()

    lifecycleOwner.handleLifecycleEvent(nonDestroyedState)

    assertNotNull(viewHolder.get())
  }
}
