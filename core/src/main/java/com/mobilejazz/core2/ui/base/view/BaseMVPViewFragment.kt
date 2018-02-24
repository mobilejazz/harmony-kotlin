package com.mobilejazz.core2.ui.base.view

import android.os.Bundle
import android.view.View
import com.mobilejazz.core2.ui.base.BaseFragment
import com.mobilejazz.core2.ui.base.presenter.Presenter
import javax.inject.Inject


abstract class BaseMVPViewFragment<T : Presenter<V>, in V : MVPView> : BaseFragment(), MVPView {

  @Inject lateinit var presenter: T

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    presenter.attachView(this@BaseMVPViewFragment as V)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
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
