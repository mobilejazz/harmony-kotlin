package com.mobilejazz.kotlin.core.viewmodel

import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.mobilejazz.kotlin.core.di.Injectable
import javax.inject.Inject


abstract class VMBaseFragment : Fragment(), Injectable, () -> Lifecycle {

  @Inject
  lateinit var viewModelFactory: ViewModelProvider.Factory

  inline fun <reified VM : ViewModel> getViewModel(): VM = ViewModelProviders.of(this, viewModelFactory).get(VM::class.java)

  override fun invoke(): Lifecycle = lifecycle
}