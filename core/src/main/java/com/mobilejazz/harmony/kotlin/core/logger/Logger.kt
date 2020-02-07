package com.mobilejazz.harmony.kotlin.core.logger

/** Logging interface.  */
interface Logger {

  enum class Level {
    VERBOSE,
    DEBUG,
    INFO,
    WARNING,
    ERROR
  }


  /** Logs a String object using a given level.  */
  fun log(level: Level, tag: String? = null, message: String)

  /** Logs a String and Throwable object using a given level.  */
  fun log(level: Level, throwable: Throwable, tag: String? = null, message: String)

  /** Logs a key-value pair.  */
  fun log(key: String, value: Any?)


  /** DEFAULT IMPLEMENTATIONS  */

  /** Log a verbose message with optional format args.  */
  fun v(tag: String, message: String, vararg args: Any) {
    this.log(Level.VERBOSE, tag, message)
  }

  /** Log a verbose exception and a message with optional format args.  */
  fun v(tag: String, t: Throwable, message: String, vararg args: Any) {
    this.log(Level.VERBOSE, t, tag, message)
  }

  /** Log a verbose message with optional format args without tag.  */
  fun v(message: String, vararg args: Any) {
    this.log(Level.VERBOSE, null, message)
  }

  /** Log a debug message with optional format args.  */
  fun d(tag: String, message: String, vararg args: Any) {
    this.log(Level.DEBUG, tag, message)
  }

  /** Log a debug exception and a message with optional format args.  */
  fun d(tag: String, t: Throwable, message: String, vararg args: Any) {
    this.log(Level.DEBUG, t, tag, message)
  }

  /** Log a debug message with optional format args.  */
  fun d(message: String, vararg args: Any) {
    this.log(Level.DEBUG, null, message)
  }

  /** Log an info message with optional format args.  */
  fun i(tag: String, message: String, vararg args: Any) {
    this.log(Level.INFO, tag, message)
  }

  /** Log an info exception and a message with optional format args.  */
  fun i(tag: String, t: Throwable, message: String, vararg args: Any) {
    this.log(Level.INFO, t, tag, message)
  }

  /** Log an info message with optional format args.  */
  fun i(message: String, vararg args: Any) {
    this.log(Level.INFO, null, message)
  }

  /** Log a warning message with optional format args.  */
  fun w(tag: String, message: String, vararg args: Any) {
    this.log(Level.WARNING, tag, message)
  }

  /** Log a warning exception and a message with optional format args.  */
  fun w(tag: String, t: Throwable, message: String, vararg args: Any) {
    this.log(Level.WARNING, t, tag, message)
  }

  /** Log a warning message with optional format args.  */
  fun w(message: String, vararg args: Any) {
    this.log(Level.WARNING, null, message)
  }

  /** Log an error message with optional format args.  */
  fun e(tag: String, message: String, vararg args: Any) {
    this.log(Level.ERROR, tag, message)
  }

  /** Log an error exception and a message with optional format args.  */
  fun e(tag: String, t: Throwable, message: String, vararg args: Any) {
    this.log(Level.ERROR, t, tag, message)
  }

  /** Log an error message with optional format args.  */
  fun e(message: String, vararg args: Any) {
    this.log(Level.ERROR, null, message)
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
