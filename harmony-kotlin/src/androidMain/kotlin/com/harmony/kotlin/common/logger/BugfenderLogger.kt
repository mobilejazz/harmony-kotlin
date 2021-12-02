package com.harmony.kotlin.common.logger

import android.content.Context
import android.os.Build
import com.bugfender.sdk.Bugfender
import java.util.regex.Pattern

open class BugfenderLogger(
  context: Context,
  applicationKey: String,
  isDebug: Boolean
) : Logger {
  init {
    Bugfender.init(context, applicationKey, isDebug)
  }

  companion object {
    private const val MAX_TAG_LENGTH = 23
    private val ANONYMOUS_CLASS = Pattern.compile("(\\$\\d+)+$")
  }

  override fun log(level: Logger.Level, tag: String?, message: String) {
    val t = tag ?: createClassTag()

    when (level) {
      Logger.Level.VERBOSE -> Bugfender.d(t, message)
      Logger.Level.DEBUG -> Bugfender.d(t, message)
      Logger.Level.INFO -> Bugfender.i(t, message)
      Logger.Level.WARNING -> Bugfender.w(t, message)
      Logger.Level.ERROR -> Bugfender.e(t, message)
    }
  }

  override fun log(level: Logger.Level, throwable: Throwable, tag: String?, message: String) {
    val t = tag ?: createClassTag()

    when (level) {
      Logger.Level.VERBOSE -> {
        Bugfender.d(t, throwable.stackTraceToString())
        Bugfender.d(t, message)
      }
      Logger.Level.DEBUG -> {
        Bugfender.d(t, throwable.stackTraceToString())
        Bugfender.d(t, message)
      }
      Logger.Level.INFO -> {
        Bugfender.i(t, throwable.stackTraceToString())
        Bugfender.i(t, message)
      }
      Logger.Level.WARNING -> {
        Bugfender.w(t, throwable.stackTraceToString())
        Bugfender.w(t, message)
      }
      Logger.Level.ERROR -> {
        Bugfender.e(t, throwable.stackTraceToString())
        Bugfender.e(t, message)
      }
    }
  }

  override fun log(key: String, value: Any?) {
    value?.let {
      log(Logger.Level.INFO, "KEY", "$key: $it")
    } ?: log(Logger.Level.INFO, "KEY", "$key: null")
  }

  override fun sendIssue(tag: String, message: String) {
    Bugfender.sendIssue(tag, message)
  }

  override fun setDeviceBoolean(key: String, value: Boolean) {
    Bugfender.setDeviceBoolean(key, value)
  }

  override fun setDeviceString(key: String, value: String) {
    Bugfender.setDeviceString(key, value)
  }

  override fun setDeviceInteger(key: String, value: Int) {
    Bugfender.setDeviceInteger(key, value)
  }

  override fun setDeviceFloat(key: String, value: Float) {
    Bugfender.setDeviceFloat(key, value)
  }

  override fun removeDeviceKey(key: String) {
    Bugfender.removeDeviceKey(key)
  }

  override val deviceIdentifier: String
    get() = Bugfender.getDeviceUrl().toString()

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
