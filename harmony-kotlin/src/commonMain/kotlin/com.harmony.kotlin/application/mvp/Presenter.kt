package com.harmony.kotlin.application.mvp

interface Presenter<in T : MVPView> {

  fun onCreate(bundle: Map<String, Any> = emptyMap())

  fun onResume()

  fun onPause()

  fun onDestroy()

  fun attachView(view: T)

  fun detachView()
}
