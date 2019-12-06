package com.harmony.kotlin.android.application.mvp

import android.os.Bundle
import com.harmony.kotlin.android.application.BaseActivity
import com.harmony.kotlin.application.mvp.MVPView
import com.harmony.kotlin.application.mvp.Presenter
import java.util.Collections.emptyMap

abstract class BaseMVPActivity<P : Presenter<V>, in V : MVPView> : BaseActivity(), MVPView {

  abstract val presenter: P

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
