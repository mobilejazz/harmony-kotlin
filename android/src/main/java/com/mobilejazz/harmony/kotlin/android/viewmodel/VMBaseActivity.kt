package com.mobilejazz.harmony.kotlin.android.viewmodel

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import com.mobilejazz.harmony.kotlin.android.di.Injectable
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import com.mobilejazz.harmony.kotlin.android.ext.dp
import com.mobilejazz.harmony.kotlin.android.ext.sp
import javax.inject.Inject

abstract class VMBaseActivity : AppCompatActivity(), HasSupportFragmentInjector, () -> Lifecycle, Injectable {

  @Inject
  lateinit var viewModelFactory: ViewModelProvider.Factory

  @Inject
  lateinit var fragmentInjector: DispatchingAndroidInjector<Fragment>

  override fun supportFragmentInjector(): AndroidInjector<Fragment> = fragmentInjector

  inline fun <reified VM : ViewModel> getViewModel(): VM = ViewModelProviders.of(this, viewModelFactory).get(VM::class.java)

  override fun invoke(): Lifecycle = lifecycle

  val Int.sp
    get() = sp(this@VMBaseActivity).toInt()

  val Int.dp
    get() = dp(this@VMBaseActivity).toInt()

}