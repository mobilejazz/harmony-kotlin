package com.mobilejazz.harmony.kotlin.core.logger


class ConsoleLogger : Logger {

  override fun log(level: Logger.Level, tag: String?, message: String) {
    tag?.let {
      println("${level.levelStringRepresentation()} - [$it]: $message")
    } ?: println("${level.levelStringRepresentation()}: $message")
  }

  override fun log(level: Logger.Level, throwable: Throwable, tag: String?, message: String) {
    log(level, tag, message)
    throwable.printStackTrace()
  }

  override fun log(key: String, value: Any?) {
    value?.let {
      log(Logger.Level.INFO, "KEY", "$key: $it")
    } ?: log(Logger.Level.INFO, "KEY", "$key: null")
  }

  override fun sendIssue(tag: String, message: String) {
    this.log(Logger.Level.DEBUG, tag, message)
  }

  override fun setDeviceBoolean(key: String, value: Boolean) {
    this.log(Logger.Level.DEBUG, key, value.toString())
  }

  override fun setDeviceString(key: String, value: String) {
    this.log(Logger.Level.DEBUG, key, value)
  }

  override fun setDeviceInteger(key: String, value: Int) {
    this.log(Logger.Level.DEBUG, key, value.toString())
  }

  override fun setDeviceFloat(key: String, value: Float) {
    this.log(Logger.Level.DEBUG, key, value.toString())
  }

  override fun removeDeviceKey(key: String) {
    this.log(Logger.Level.DEBUG, "RemoveDeviceKey", key)
  }

  override val deviceIdentifier: String
    get() = ""

  private fun Logger.Level.levelStringRepresentation(): String {
    return when (this) {
      Logger.Level.VERBOSE -> "VERBOSE"
      Logger.Level.DEBUG -> "DEBUG"
      Logger.Level.INFO -> "INFO"
      Logger.Level.WARNING -> "WARNING"
      Logger.Level.ERROR -> "ERROR"
    }
  }
}
