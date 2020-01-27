package com.mobilejazz.harmony.kotlin.core.logger

/** Logging interface.  */
interface Logger {

  enum class LogLevel {
    VERBOSE,
    DEBUG,
    INFO,
    WARNING,
    ERROR
  }


  /** Logs a String object using a given level.  */
  fun log(level: LogLevel, tag: String? = null, message: String)

  /** Logs a String and Throwable object using a given level.  */
  fun log(level: LogLevel, throwable: Throwable, tag: String? = null, message: String)

  /** Logs a key-value pair.  */
  fun log(key: String, value: Any?)


  /** DEFAULT IMPLEMENTATIONS  */

  /** Log a verbose message with optional format args.  */
  fun v(tag: String, message: String, vararg args: Any) {
    this.log(LogLevel.VERBOSE, tag, message)
  }

  /** Log a verbose exception and a message with optional format args.  */
  fun v(tag: String, t: Throwable, message: String, vararg args: Any) {
    this.log(LogLevel.VERBOSE, t, tag, message)
  }

  /** Log a verbose message with optional format args without tag.  */
  fun v(message: String, vararg args: Any) {
    this.log(LogLevel.VERBOSE, null, message)
  }

  /** Log a debug message with optional format args.  */
  fun d(tag: String, message: String, vararg args: Any) {
    this.log(LogLevel.DEBUG, tag, message)
  }

  /** Log a debug exception and a message with optional format args.  */
  fun d(tag: String, t: Throwable, message: String, vararg args: Any) {
    this.log(LogLevel.DEBUG, t, tag, message)
  }

  /** Log a debug message with optional format args.  */
  fun d(message: String, vararg args: Any) {
    this.log(LogLevel.DEBUG, null, message)
  }

  /** Log an info message with optional format args.  */
  fun i(tag: String, message: String, vararg args: Any) {
    this.log(LogLevel.INFO, tag, message)
  }

  /** Log an info exception and a message with optional format args.  */
  fun i(tag: String, t: Throwable, message: String, vararg args: Any) {
    this.log(LogLevel.INFO, t, tag, message)
  }

  /** Log an info message with optional format args.  */
  fun i(message: String, vararg args: Any) {
    this.log(LogLevel.INFO, null, message)
  }

  /** Log a warning message with optional format args.  */
  fun w(tag: String, message: String, vararg args: Any) {
    this.log(LogLevel.WARNING, tag, message)
  }

  /** Log a warning exception and a message with optional format args.  */
  fun w(tag: String, t: Throwable, message: String, vararg args: Any) {
    this.log(LogLevel.WARNING, t, tag, message)
  }

  /** Log a warning message with optional format args.  */
  fun w(message: String, vararg args: Any) {
    this.log(LogLevel.WARNING, null, message)
  }

  /** Log an error message with optional format args.  */
  fun e(tag: String, message: String, vararg args: Any) {
    this.log(LogLevel.ERROR, tag, message)
  }

  /** Log an error exception and a message with optional format args.  */
  fun e(tag: String, t: Throwable, message: String, vararg args: Any) {
    this.log(LogLevel.ERROR, t, tag, message)
  }

  /** Log an error message with optional format args.  */
  fun e(message: String, vararg args: Any) {
    this.log(LogLevel.ERROR, null, message)
  }

  /** Method to send issue to external servers or local files.  */
  fun sendIssue(tag: String, message: String)

  /** Sets a device detail with boolean type.  */
  fun setDeviceBoolean(key: String, value: Boolean)

  /** Sets a device detail with string type.  */
  fun setDeviceString(key: String, value: String)

  /** Sets a device detail with integer type.  */
  fun setDeviceInteger(key: String, value: Int)

  /** Sets a device detail with double type.  */

  fun setDeviceFloat(key: String, value: Float)

  /** Remove a device detail.  */
  fun removeDeviceKey(key: String)

  /** Get the device identifier generated.  */
  val deviceIdentifier: String
}
