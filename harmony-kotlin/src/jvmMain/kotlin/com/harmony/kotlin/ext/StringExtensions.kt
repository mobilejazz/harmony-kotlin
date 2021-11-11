package com.harmony.kotlin.ext

import java.security.SecureRandom

/**
 * Creates a random hexadecimal [String]
 * @param size desired size for the generated String. If size is a odd number the final size will be the previous even number
 * @return a random hexadecimal String
 */
fun String.Companion.generateRandomHexString(size: Int): String {
  val bytes = ByteArray(size / 2)
  SecureRandom().nextBytes(bytes)
  return bytes.toHexString()
}
