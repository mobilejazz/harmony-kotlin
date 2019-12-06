package com.harmony.kotlin.android.application.vm

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.harmony.kotlin.android.application.di.Injectable
import com.harmony.kotlin.android.application.ext.dp
import com.harmony.kotlin.android.application.ext.sp
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
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