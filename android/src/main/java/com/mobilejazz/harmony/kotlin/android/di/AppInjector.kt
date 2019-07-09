package com.mobilejazz.harmony.kotlin.android.di

import android.app.Activity
import android.app.Application
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.mobilejazz.harmony.kotlin.android.viewmodel.VMBaseActivity


fun Application.initInjection() {

  this.registerActivityLifecycleCallbacks(object : Application.ActivityLifecycleCallbacks {
    override fun onActivityPaused(activity: Activity?) {
    }

    override fun onActivityResumed(activity: Activity?) {
    }

    override fun onActivityStarted(activity: Activity?) {
    }

    override fun onActivityDestroyed(activity: Activity?) {
    }

    override fun onActivitySaveInstanceState(activity: Activity?, bundle: Bundle?) {
    }

    override fun onActivityStopped(activity: Activity?) {
    }

    override fun onActivityCreated(activity: Activity, bundle: Bundle?) {
      if (activity is VMBaseActivity) {
        dagger.android.AndroidInjection.inject(activity)
        activity.supportFragmentManager.registerFragmentLifecycleCallbacks(
            object : FragmentManager.FragmentLifecycleCallbacks() {
              override fun onFragmentCreated(fm: FragmentManager, f: Fragment, savedInstanceState: Bundle?) {
                if (f is Injectable) {
                  dagger.android.support.AndroidSupportInjection.inject(f)
                }
              }
            }, true)
      }
    }
  })
}
