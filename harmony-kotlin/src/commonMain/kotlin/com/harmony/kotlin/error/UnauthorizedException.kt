package com.harmony.kotlin.error

/**
 * Not authorized to access the data
 */
data class UnauthorizedException(
  override val message: String? = null,
  override val cause: Throwable,
  val isResolved: Boolean
) : HarmonyException(message, cause)
