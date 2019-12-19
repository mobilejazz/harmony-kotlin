package com.mobilejazz.harmony.kotlin.android.ui.base

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.AndroidSupportInjection
import dagger.android.support.HasSupportFragmentInjector
import javax.inject.Inject

abstract class BaseFragment : Fragment(), HasSupportFragmentInjector {

  @Inject
  lateinit var childFragmentInjector: DispatchingAndroidInjector<Fragment>

  override fun supportFragmentInjector(): AndroidInjector<Fragment> = childFragmentInjector

  open fun injectDependencies() {
    try {
      AndroidSupportInjection.inject(this)
    } catch (t: Throwable) {
    }
  }

  override fun onAttach(context: Context) {
    injectDependencies()
    super.onAttach(context)
  }

  override fun onCreateView(
      inflater: LayoutInflater,
      container: ViewGroup?,
      savedInstanceState: Bundle?
  ): View? {
    return inflater.inflate(getContentViewResId(), container, false)
  }

  @LayoutRes
  abstract fun getContentViewResId(): Int

}
