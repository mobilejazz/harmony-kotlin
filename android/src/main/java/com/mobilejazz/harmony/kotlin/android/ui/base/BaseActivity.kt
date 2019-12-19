package com.mobilejazz.harmony.kotlin.android.ui.base

import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import dagger.android.AndroidInjection
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import javax.inject.Inject

abstract class BaseActivity : AppCompatActivity(), HasSupportFragmentInjector {

  @Inject
  lateinit var fragmentInjector: DispatchingAndroidInjector<Fragment>

  open fun injectDependencies() {
    try {
      AndroidInjection.inject(this)
    } catch (t: Throwable) {
    }
  }


  override fun onCreate(savedInstanceState: Bundle?) {
    injectDependencies()
    super.onCreate(savedInstanceState)
    setContentView(getContentViewResId())
  }

  @LayoutRes
  abstract fun getContentViewResId(): Int

  override fun supportFragmentInjector(): AndroidInjector<Fragment>? = fragmentInjector

}
