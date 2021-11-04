package com.harmony.kotlin.common.logger

import android.os.Build
import android.util.Log
import java.util.regex.Pattern

open class AndroidLogger(private val isDebug: Boolean) : Logger {

  companion object {
    private const val MAX_TAG_LENGTH = 23
    private val ANONYMOUS_CLASS = Pattern.compile("(\\$\\d+)+$")
  }

  override fun log(level: Logger.Level, tag: String?, message: String) {
    val tag = tag ?: createClassTag()
    if (isDebug) {
      when (level) {
        Logger.Level.VERBOSE -> Log.v(tag, message)
        Logger.Level.DEBUG -> Log.d(tag, message)
        Logger.Level.INFO -> Log.i(tag, message)
        Logger.Level.WARNING -> Log.w(tag, message)
        Logger.Level.ERROR -> Log.e(tag, message)
      }
    }
  }

  override fun log(level: Logger.Level, throwable: Throwable, tag: String?, message: String) {
    val tag = tag ?: createClassTag()
    if (isDebug) {
      when (level) {
        Logger.Level.VERBOSE -> Log.v(tag, message, throwable)
        Logger.Level.DEBUG -> Log.d(tag, message, throwable)
        Logger.Level.INFO -> Log.i(tag, message, throwable)
        Logger.Level.WARNING -> Log.w(tag, message, throwable)
        Logger.Level.ERROR -> Log.e(tag, message, throwable)
      }
    }
  }

  override fun log(key: String, value: Any?) {
    value?.let {
      log(Logger.Level.INFO, "KEY", "$key: $it")
    } ?: log(Logger.Level.INFO, "KEY", "$key: null")
  }

  override fun sendIssue(tag: String, message: String) {
    log(Logger.Level.DEBUG, tag, message)
  }

  override fun setDeviceBoolean(key: String, value: Boolean) {
    log(Logger.Level.DEBUG, key, value.toString())
  }

  override fun setDeviceString(key: String, value: String) {
    log(Logger.Level.DEBUG, key, value)
  }

  override fun setDeviceInteger(key: String, value: Int) {
    log(Logger.Level.DEBUG, key, value.toString())
  }

  override fun setDeviceFloat(key: String, value: Float) {
    log(Logger.Level.DEBUG, key, value.toString())
  }

  override fun removeDeviceKey(key: String) {
    log(Logger.Level.DEBUG, key, "RemoveDeviceKey")
  }

  override val deviceIdentifier: String
    get() = ""

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
