package com.harmony.kotlin.android.application.mvp

import android.os.Bundle
import android.view.View
import com.harmony.kotlin.android.application.BaseFragment
import com.harmony.kotlin.application.mvp.MVPView
import com.harmony.kotlin.application.mvp.Presenter

abstract class BaseMVPViewFragment<T : Presenter<V>, in V : MVPView> : BaseFragment(), MVPView {

  abstract val presenter: T

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    @Suppress("UNCHECKED_CAST")
    presenter.attachView(this@BaseMVPViewFragment as V)
    presenter.onCreate(onSetupPresenterArguments())
  }

  override fun onResume() {
    super.onResume()
    presenter.onResume()
  }

  override fun onPause() {
    super.onPause()
    presenter.onPause()
  }

  override fun onDestroyView() {
    presenter.onDestroy()
    presenter.detachView()
    super.onDestroyView()
  }

  protected open fun onSetupPresenterArguments(): Map<String, Any> = emptyMap()
}
