package com.mobilejazz.harmony.kotlin.android.viewmodel

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.mobilejazz.harmony.kotlin.android.di.Injectable
import com.mobilejazz.harmony.kotlin.android.ext.dp
import com.mobilejazz.harmony.kotlin.android.ext.sp
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import javax.inject.Inject

abstract class VMBaseActivity : AppCompatActivity(), HasAndroidInjector, () -> Lifecycle, Injectable {

  @Inject
  lateinit var viewModelFactory: ViewModelProvider.Factory

  @Inject
  lateinit var fragmentInjector: DispatchingAndroidInjector<Any>

  override fun androidInjector(): AndroidInjector<Any> = fragmentInjector

  inline fun <reified VM : ViewModel> getViewModel(): VM = ViewModelProviders.of(this, viewModelFactory).get(VM::class.java)

  override fun invoke(): Lifecycle = lifecycle

  val Int.sp
    get() = sp(this@VMBaseActivity).toInt()

  val Int.dp
    get() = dp(this@VMBaseActivity).toInt()

}