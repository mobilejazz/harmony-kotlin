package com.mobilejazz.kotlin.core.ui.base.view

import android.os.Bundle
import com.mobilejazz.kotlin.core.ui.base.BaseActivity
import com.mobilejazz.kotlin.core.ui.base.presenter.Presenter
import java.util.Collections.emptyMap
import javax.inject.Inject

abstract class BaseMVPActivity<T : Presenter<V>, in V : MVPView> : BaseActivity(), MVPView {

  @Inject lateinit var presenter: T

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    presenter.attachView(this@BaseMVPActivity as V)
    presenter.onCreate(onSetupPresenterArguments())
  }

  override fun onResume() {
    super.onResume()
    presenter.onResume()
  }

  override fun onPause() {
    presenter.onPause()
    super.onPause()
  }

  override fun onDestroy() {
    presenter.onDestroy()
    presenter.detachView()
    super.onDestroy()
  }

  protected open fun onSetupPresenterArguments(): Map<String, Any> = emptyMap()

  companion object {
    const val PRESENTER_ARGUMENTS = "presenter.arguments"
  }

}
