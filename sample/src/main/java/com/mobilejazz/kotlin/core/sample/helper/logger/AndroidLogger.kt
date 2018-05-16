package com.mobilejazz.kotlin.core.sample.helper.logger

import android.util.Log
import com.mobilejazz.kotlin.core.sample.BuildConfig
import com.mobilejazz.logger.library.Logger

class AndroidLogger : Logger {

  override fun v(tag: String, message: String, vararg args: Any) {
    if (BuildConfig.DEBUG) {
      Log.v(tag, message)
    }
  }

  override fun v(tag: String, t: Throwable, message: String, vararg args: Any) {
    if (BuildConfig.DEBUG) {
      Log.v(tag, message, t)
    }
  }

  override fun d(tag: String, message: String, vararg args: Any) {
    if (BuildConfig.DEBUG) {
      Log.d(tag, message)
    }
  }

  override fun d(tag: String, t: Throwable, message: String, vararg args: Any) {
    if (BuildConfig.DEBUG) {
      Log.d(tag, message, t)
    }
  }

  override fun i(tag: String, message: String, vararg args: Any) {
    if (BuildConfig.DEBUG) {
      Log.i(tag, message)
    }
  }

  override fun i(tag: String, t: Throwable, message: String, vararg args: Any) {
    if (BuildConfig.DEBUG) {
      Log.i(tag, message, t)
    }
  }

  override fun w(tag: String, message: String, vararg args: Any) {
    if (BuildConfig.DEBUG) {
      Log.w(tag, message)
    }
  }

  override fun w(tag: String, t: Throwable, message: String, vararg args: Any) {
    if (BuildConfig.DEBUG) {
      Log.w(tag, message, t)
    }
  }

  override fun e(tag: String, message: String, vararg args: Any) {
    if (BuildConfig.DEBUG) {
      Log.e(tag, message)
    }
  }

  override fun e(tag: String, t: Throwable, message: String, vararg args: Any) {
    if (BuildConfig.DEBUG) {
      Log.e(tag, message, t)
    }
  }

  override fun sendIssue(tag: String, message: String) {
    // Nothing to do
  }

  override fun setDeviceBoolean(key: String, value: Boolean) {
    // Nothing to do
  }

  override fun setDeviceString(key: String, value: String) {
    // Nothing to do
  }

  override fun setDeviceInteger(key: String, value: Int?) {
    // Nothing to do
  }

  override fun setDeviceFloat(key: String, value: Float?) {
    // Nothing to do
  }

  override fun removeDeviceKey(key: String) {
    // Nothing to do
  }

  override fun getDeviceIdentifier(): String {
    return ""
  }
}
