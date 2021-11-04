package com.harmony.kotlin.common.exceptions

import com.harmony.kotlin.common.logger.Logger

/**
 * Try this block and re-throw any Exception while logging it.
 */
inline fun <R> tryOrThrow(
  logger: Logger,
  tag: String,
  message: String = "Exception logged",
  level: Logger.Level = Logger.Level
    .WARNING,
  block: () -> R
):
  R {
  return try {
    block()
  } catch (e: Exception) {
    logger.log(level, e, tag, message)
    throw e
  }
}

/**
 * Try this block and catch any Exception.
 *
 * Optionally a logger and some parameters can be passed to log the Exception
 */
inline fun <R> tryOrCatch(
  logger: Logger? = null,
  tag: String = "TryOrCatch",
  message: String = "Exception logged",
  level: Logger.Level = Logger.Level.WARNING,
  block: () -> R
) {
  return try {
    block()
    Unit
  } catch (e: Exception) {
    logger?.log(level, e, tag, message)
    Unit
  }
}

/**
 * Try this block and return null if there is any Exception.
 *
 * Optionally a logger and some parameters can be passed to log the Exception
 */
inline fun <R> tryOrNull(
  logger: Logger? = null,
  tag: String = "TryOrNull",
  message: String = "Exception logged",
  level: Logger.Level = Logger.Level.WARNING,
  block: () -> R
): R? {
  return try {
    block()
  } catch (e: Exception) {
    logger?.log(level, e, tag, message)
    null
  }
}

/**
 * Try this block and return the provided defaultValue if there is any Exception.
 *
 * Optionally a logger and some parameters can be passed to log the Exception
 */
inline fun <R> tryOrDefault(
  logger: Logger? = null,
  tag: String = "TryOrDefault",
  message: String = "Exception logged",
  level: Logger.Level = Logger.Level.WARNING,
  defaultValue: R,
  block: () -> R
): R {
  return try {
    block()
  } catch (e: Exception) {
    logger?.log(level, e, tag, message)
    defaultValue
  }
}
