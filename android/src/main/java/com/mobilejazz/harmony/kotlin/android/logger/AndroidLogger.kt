package com.mobilejazz.harmony.kotlin.android.logger

import android.os.Build
import android.util.Log
import com.mobilejazz.harmony.kotlin.android.BuildConfig
import com.mobilejazz.harmony.kotlin.core.logger.Logger
import java.util.regex.Pattern

open class AndroidLogger : Logger {

  companion object {
    private const val MAX_TAG_LENGTH = 23
    private val ANONYMOUS_CLASS = Pattern.compile("(\\$\\d+)+$")
  }

  override fun log(level: Logger.LogLevel, tag: String?, message: String) {
    val tag = tag ?: createClassTag()
    if (BuildConfig.DEBUG) {
      when (level) {
        Logger.LogLevel.VERBOSE -> Log.v(tag, message)
        Logger.LogLevel.DEBUG -> Log.d(tag, message)
        Logger.LogLevel.INFO -> Log.i(tag, message)
        Logger.LogLevel.WARNING -> Log.w(tag, message)
        Logger.LogLevel.ERROR -> Log.e(tag, message)
      }
    }
  }

  override fun log(level: Logger.LogLevel, throwable: Throwable, tag: String?, message: String) {
    val tag = tag ?: createClassTag()
    if (BuildConfig.DEBUG) {
      when (level) {
        Logger.LogLevel.VERBOSE -> Log.v(tag, message, throwable)
        Logger.LogLevel.DEBUG -> Log.d(tag, message, throwable)
        Logger.LogLevel.INFO -> Log.i(tag, message, throwable)
        Logger.LogLevel.WARNING -> Log.w(tag, message, throwable)
        Logger.LogLevel.ERROR -> Log.e(tag, message, throwable)
      }
    }
  }

  override fun log(key: String, value: Any?) {
    value?.let {
      log(Logger.LogLevel.INFO, "KEY", "$key: $it")
    } ?: log(Logger.LogLevel.INFO, "KEY", "$key: null")
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

  override fun setDeviceInteger(key: String, value: Int) {
    // Nothing to do
  }

  override fun setDeviceFloat(key: String, value: Float) {
    // Nothing to do
  }

  override fun removeDeviceKey(key: String) {
    // Nothing to do
  }

  override val deviceIdentifier: String
    get() = TODO("not implemented")

  private fun createClassTag(): String? {
    val ignoreClass = listOf(
        AndroidLogger::class.java.name,
        Logger::class.java.name
    )
    val stackTraceElement: StackTraceElement = Throwable().stackTrace
        .first { it.className !in ignoreClass && !it.className.contains("DefaultImpls") }
    var tag = stackTraceElement.className.substringAfterLast('.')
    val m = ANONYMOUS_CLASS.matcher(tag)
    if (m.find()) {
      tag = m.replaceAll("")
    }
    // Tag length limit was removed in API 24.
    return if (tag.length <= MAX_TAG_LENGTH || Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
      tag
    } else {
      tag.substring(0, MAX_TAG_LENGTH)
    }
  }

}
