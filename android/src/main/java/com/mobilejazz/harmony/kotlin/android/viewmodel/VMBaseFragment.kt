package com.mobilejazz.harmony.kotlin.android.viewmodel

import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.mobilejazz.harmony.kotlin.android.di.Injectable
import com.mobilejazz.harmony.kotlin.android.ext.dp
import com.mobilejazz.harmony.kotlin.android.ext.sp
import javax.inject.Inject


abstract class VMBaseFragment : Fragment(), Injectable, () -> Lifecycle {

  @Inject
  lateinit var viewModelFactory: ViewModelProvider.Factory

  inline fun <reified VM : ViewModel> getViewModel(): VM = ViewModelProviders.of(this, viewModelFactory).get(VM::class.java)

  override fun invoke(): Lifecycle = lifecycle

  val Int.sp
    get() = sp(context!!).toInt()

  val Int.dp
    get() = dp(context!!).toInt()
}