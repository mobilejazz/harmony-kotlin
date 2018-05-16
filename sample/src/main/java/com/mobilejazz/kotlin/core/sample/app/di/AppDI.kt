package com.mobilejazz.kotlin.core.sample.app.di

import android.app.Application
import android.content.Context
import com.mobilejazz.kotlin.core.sample.app.di.general.InteractorsModule
import com.mobilejazz.kotlin.core.sample.app.di.general.ItemsModule
import com.mobilejazz.kotlin.core.sample.app.di.general.LoggerModule
import com.mobilejazz.kotlin.core.sample.app.di.general.NetworkingModule
import com.mobilejazz.kotlin.core.sample.app.ui.App
import dagger.Binds
import dagger.Component
import dagger.Module
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import org.worldreader.classroom.app.di.ui.ActivityBindingsModule
import javax.inject.Singleton

@Module(
    includes = [
      (AndroidSupportInjectionModule::class),
      (InteractorsModule::class),
      (NetworkingModule::class),
      (ItemsModule::class),
      (LoggerModule::class)
    ]
)
internal abstract class AppModule {

  @Binds
  @Singleton
  internal abstract fun application(app: App): Application

  @Binds
  @Singleton
  internal abstract fun context(app: App): Context
}

@Singleton
@Component(modules = [(AppModule::class), (ActivityBindingsModule::class)])
internal interface AppComponent : AndroidInjector<App> {

  @Component.Builder
  abstract class Builder : AndroidInjector.Builder<App>()
}