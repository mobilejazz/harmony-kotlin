package com.mobilejazz.harmony.kotlin.android.ui.base

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import com.mobilejazz.harmony.kotlin.core.ext.getStackTraceAsString
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

abstract class BaseFragment : Fragment(), HasAndroidInjector {

  @Inject
  lateinit var childFragmentInjector: DispatchingAndroidInjector<Any>

  override fun androidInjector(): AndroidInjector<Any> = childFragmentInjector


  open fun injectDependencies() {
    try {
      AndroidSupportInjection.inject(this)
    } catch (t: Throwable) {
      Log.d("BaseFragment",
          "Dagger is not configured for ${this::class.java.name}. If you are not using Dagger in this Fragment don't worry about this.\n" +
              "Exception thrown by Dagger: ${t.getStackTraceAsString()}")
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
