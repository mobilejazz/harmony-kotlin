package com.harmony.kotlin.common.logger

/**
 * Dummy class to be provided as logger when you actually don't want anything to be logged (for example in unit tests)
 */
class VoidLogger : Logger {
  override fun log(level: Logger.Level, tag: String?, message: String) = Unit
  override fun log(level: Logger.Level, throwable: Throwable, tag: String?, message: String) = Unit
  override fun log(key: String, value: Any?) = Unit
  override fun sendIssue(tag: String, message: String) = Unit
  override fun setDeviceBoolean(key: String, value: Boolean) = Unit
  override fun setDeviceString(key: String, value: String) = Unit
  override fun setDeviceInteger(key: String, value: Int) = Unit
  override fun setDeviceFloat(key: String, value: Float) = Unit
  override fun removeDeviceKey(key: String) = Unit
  override val deviceIdentifier: String = ""
}
