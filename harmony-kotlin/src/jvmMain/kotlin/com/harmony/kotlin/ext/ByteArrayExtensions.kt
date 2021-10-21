package com.harmony.kotlin.ext

/**
 * Transforms the [ByteArray] into a hexadecimal [String]
 * @return a string with the hexadecimal representation of the ByteArray
 */
fun ByteArray.toHexString(): String {
  val sb = StringBuilder()
  for (aByte in this) {
    sb.append(String.format("%02x", aByte))
  }

  return sb.toString()
}
