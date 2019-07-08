package com.mobilejazz.sample.di.general

import android.content.Context
import android.content.SharedPreferences
import dagger.Module
import dagger.Provides
import dagger.Subcomponent
import javax.inject.Singleton

@Module(subcomponents = [(AndroidComponent::class)])
class AndroidModule {

  @Provides
  @Singleton
  fun provideSharedPreferences(c: Context): SharedPreferences = c.getSharedPreferences("items-preferences", Context.MODE_PRIVATE)
}

@Subcomponent
interface AndroidComponent {

  fun sharedPreferences(): SharedPreferences

  @Subcomponent.Builder
  interface Builder {
    fun build(): AndroidComponent
  }

}