package com.mobilejazz.kotlin.core.sample.app.di

import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import javax.inject.Scope

@Scope
@Retention(RetentionPolicy.RUNTIME) annotation class ActivityScope

@Scope
@Retention(RetentionPolicy.RUNTIME) annotation class FragmentScope

@Scope
@Retention(RetentionPolicy.RUNTIME) annotation class FragmentChildScope