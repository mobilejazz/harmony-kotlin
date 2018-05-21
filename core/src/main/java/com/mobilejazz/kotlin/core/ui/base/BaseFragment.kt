package com.mobilejazz.kotlin.core.ui.base

import android.content.Context
import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import butterknife.ButterKnife
import butterknife.Unbinder
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.AndroidSupportInjection
import dagger.android.support.HasSupportFragmentInjector
import javax.inject.Inject

abstract class BaseFragment : Fragment(), HasSupportFragmentInjector {

  @Inject
  lateinit var childFragmentInjector: DispatchingAndroidInjector<Fragment>

  private var viewUnbinder: Unbinder? = null

  override fun supportFragmentInjector(): AndroidInjector<Fragment> = childFragmentInjector

  // Perform injection here for M (API 23) due to deprecation of onAttach(Activity).
  override fun onAttach(context: Context?) {
    AndroidSupportInjection.inject(this)
    super.onAttach(context)
  }

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    return inflater.inflate(getContentViewResId(), container, false)
  }

  @LayoutRes
  abstract fun getContentViewResId(): Int

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    viewUnbinder = ButterKnife.bind(this, getView()!!)
  }

  override fun onDestroyView() {
    viewUnbinder?.apply {
      unbind()
    }
    super.onDestroyView()
  }
}
