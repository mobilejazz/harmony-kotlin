package com.mobilejazz.harmony.kotlin.core.viewmodel

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.support.v4.app.Fragment
import com.mobilejazz.harmony.kotlin.core.di.Injectable
import com.mobilejazz.harmony.kotlin.core.ext.dp
import com.mobilejazz.harmony.kotlin.core.ext.sp
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