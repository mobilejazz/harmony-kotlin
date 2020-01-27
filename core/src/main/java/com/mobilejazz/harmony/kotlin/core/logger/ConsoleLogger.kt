package com.mobilejazz.harmony.kotlin.core.logger


class ConsoleLogger : Logger {

  override fun log(level: Logger.LogLevel, tag: String?, message: String) {
    tag?.let {
      println("${level.levelStringRepresentation()} - [$it]: $message")
    } ?: println("${level.levelStringRepresentation()}: $message")
  }

  override fun log(level: Logger.LogLevel, throwable: Throwable, tag: String?, message: String) {
    log(level, tag, message)
    throwable.printStackTrace()
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

  private fun Logger.LogLevel.levelStringRepresentation(): String {
    return when (this) {
      Logger.LogLevel.VERBOSE -> "VERBOSE"
      Logger.LogLevel.DEBUG -> "DEBUG"
      Logger.LogLevel.INFO -> "INFO"
      Logger.LogLevel.WARNING -> "WARNING"
      Logger.LogLevel.ERROR -> "ERROR"
    }
  }
}
