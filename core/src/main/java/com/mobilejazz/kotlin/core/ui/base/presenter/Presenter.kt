package com.mobilejazz.kotlin.core.ui.base.presenter

import com.mobilejazz.kotlin.core.ui.base.view.MVPView

interface Presenter<in T : MVPView> {

  fun onCreate(bundle: Map<String, Any> = emptyMap())

  fun onResume()

  fun onPause()

  fun onDestroy()

  fun attachView(view: T)

  fun detachView()
}
