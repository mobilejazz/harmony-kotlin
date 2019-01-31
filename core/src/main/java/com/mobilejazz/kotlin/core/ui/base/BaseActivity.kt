package com.mobilejazz.kotlin.core.ui.base

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

  @Inject lateinit var fragmentInjector: DispatchingAndroidInjector<Fragment>

  override fun onCreate(savedInstanceState: Bundle?) {
    AndroidInjection.inject(this)
    super.onCreate(savedInstanceState)
    setContentView(getContentViewResId())
  }

  @LayoutRes
  abstract fun getContentViewResId(): Int

  override fun supportFragmentInjector(): AndroidInjector<Fragment>? = fragmentInjector

}
