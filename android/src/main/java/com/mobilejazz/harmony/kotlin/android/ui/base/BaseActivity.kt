package com.mobilejazz.harmony.kotlin.android.ui.base

import android.os.Bundle
import android.util.Log
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import com.mobilejazz.harmony.kotlin.core.ext.getStackTraceAsString
import dagger.android.AndroidInjection
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import javax.inject.Inject

abstract class BaseActivity : AppCompatActivity(), HasAndroidInjector {

  @Inject
  lateinit var fragmentInjector: DispatchingAndroidInjector<Any>

  open fun injectDependencies() {
    try {
      AndroidInjection.inject(this)
    } catch (t: Throwable) {
      Log.d("BaseActivity",
          "Dagger is not configured for ${this::class.java.name}. If you are not using Dagger in this Activity don't worry about this.\n" +
              "Exception thrown by Dagger: ${t.getStackTraceAsString()}")
    }
  }


  override fun onCreate(savedInstanceState: Bundle?) {
    injectDependencies()
    super.onCreate(savedInstanceState)
    setContentView(getContentViewResId())
  }

  @LayoutRes
  abstract fun getContentViewResId(): Int

  override fun androidInjector(): AndroidInjector<Any> = fragmentInjector
}
