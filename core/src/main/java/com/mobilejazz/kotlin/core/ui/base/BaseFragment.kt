package com.mobilejazz.kotlin.core.ui.base

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

  @Inject lateinit var childFragmentInjector: DispatchingAndroidInjector<Fragment>

  override fun supportFragmentInjector(): AndroidInjector<Fragment> = childFragmentInjector

  // Perform injection here for M (API 23) due to deprecation of onAttach(Activity).
  override fun onAttach(context: Context?) {
    AndroidSupportInjection.inject(this)
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
