package com.mobilejazz.core2.ui.base.presenter

import com.mobilejazz.core2.ui.base.view.MVPView

interface Presenter<in T : MVPView> {

  fun onCreate(bundle: Map<String, Any> = emptyMap())

  fun onResume()

  fun onPause()

  fun onDestroy()

  fun attachView(view: T)

  fun detachView()
}
