package com.mobilejazz.harmony.kotlin.android.ui.base.presenter

import com.mobilejazz.harmony.kotlin.android.ui.base.view.MVPView

/**
 * Handle View attach/detach automatically
 */
abstract class BasePresenter<V : MVPView> : Presenter<V> {

  var view: V? = null

  override fun attachView(view: V) {
    this.view = view
  }

  override fun detachView() {
    this.view = null
  }
}

