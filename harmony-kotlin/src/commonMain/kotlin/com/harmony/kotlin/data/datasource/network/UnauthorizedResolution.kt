package com.harmony.kotlin.data.datasource.network

/**
 * Interface responsible of resolve the execution of an unauthorized error
 */
interface UnauthorizedResolution {

  /**
   * Resolve the unauthorized action
   *
   * @return if yes, means it was resolve, otherwise no.
   */
  fun resolve(): Boolean = false
}

object DefaultUnauthorizedResolution : UnauthorizedResolution
