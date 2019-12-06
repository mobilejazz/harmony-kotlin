package com.harmony.kotlin.android.data.datasource

import android.content.Context
import android.content.SharedPreferences
import android.util.Base64
import java.nio.charset.Charset

/**
 * [SharedPreferences] implementation that obfuscate both key and value using base64
 */
class Base64ObfuscatedPreferences(context: Context, name: String) : SharedPreferences {

  private val sharedPreferences = context.getSharedPreferences(name, Context.MODE_PRIVATE)

  override fun contains(key: String): Boolean = this.sharedPreferences.contains(key.toBase64())

  /**
   * Retrieve all values from the preferences.
   *
   * **NOTE** All values on this map are String or Set<String> as they are converted prior saving and there is not enough information to cast values to their
   * original types.
   *
   * @return Returns a map containing a list of pairs key/value representing the preferences.
   */
  override fun getAll(): Map<String, Any> {
    val allEntries = mutableMapOf<String, Any>()

    this.sharedPreferences.all.forEach { (key, value) ->
      try {
        val clearKey = key.fromBase64ToString()
        if (!this.contains(clearKey)) {
          throw  IllegalArgumentException("The key is not contained so it belongs to a clear text stored key/value by other SharedPrefs instance")
        }
        val clearValue: Any = when (value) {
          is String -> value.fromBase64ToString()
          is Set<*> -> (value as Set<String>).map { it.fromBase64ToString() }.toSet()
          else -> throw IllegalArgumentException("Anything different from a String or a Set of String  belongs to a clear text stored key/value by other SharedPrefs instance")
        }
        allEntries[clearKey] = clearValue
      } catch (_: IllegalArgumentException) {
      }
    }

    return allEntries
  }

  override fun getBoolean(key: String, defValue: Boolean): Boolean =
      getString(key)?.toBoolean() ?: defValue

  override fun getInt(key: String, defValue: Int): Int =
      getString(key)?.toInt() ?: defValue

  override fun getFloat(key: String, defValue: Float): Float =
      getString(key)?.toFloat() ?: defValue

  override fun getLong(key: String, defValue: Long): Long =
      getString(key)?.toLong() ?: defValue

  override fun getString(key: String, defValue: String?): String? =
      getString(key) ?: defValue

  override fun getStringSet(key: String, defValues: Set<String>?): Set<String>? =
      getStringSet(key) ?: defValues

  private fun getString(clearKey: String): String? =
      this.sharedPreferences.getString(clearKey.toBase64(), null)?.let {
        it.fromBase64ToString()
      }

  private fun getStringSet(clearKey: String): Set<String>? =
      this.sharedPreferences.getStringSet(clearKey.toBase64(), null)?.map {
        it.fromBase64ToString()
      }?.toSet()

  override fun registerOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener?) {
    this.sharedPreferences.registerOnSharedPreferenceChangeListener(listener)
  }

  override fun unregisterOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener?) {
    this.sharedPreferences.unregisterOnSharedPreferenceChangeListener(listener)
  }

  override fun edit(): SharedPreferences.Editor = Editor(this.sharedPreferences.edit())

  /**
   * [Editor] implementation that obfuscate both key and value using base64
   */
  class Editor(private val editor: SharedPreferences.Editor) : SharedPreferences.Editor {

    override fun putBoolean(key: String, value: Boolean): SharedPreferences.Editor =
        putString(key, value.toString()).let { this }

    override fun putInt(key: String, value: Int): SharedPreferences.Editor =
        putString(key, value.toString()).let { this }

    override fun putFloat(key: String, value: Float): SharedPreferences.Editor =
        putString(key, value.toString()).let { this }

    override fun putLong(key: String, value: Long): SharedPreferences.Editor =
        putString(key, value.toString()).let { this }

    override fun putString(clearKey: String, value: String?): SharedPreferences.Editor =
        this.editor.putString(clearKey.toBase64(), value?.toBase64()).let { this }

    override fun putStringSet(clearKey: String, values: Set<String>?): SharedPreferences.Editor =
        this.editor.putStringSet(
            clearKey.toBase64(), values?.map {
          it.toBase64()
        }?.toSet()
        ).let { this }

    override fun remove(clearKey: String): SharedPreferences.Editor = this.editor.remove(clearKey.toBase64()).let { this }

    override fun clear(): SharedPreferences.Editor = this.editor.clear().let { this }

    override fun commit(): Boolean = this.editor.commit()

    override fun apply() = this.editor.apply()
  }
}

private fun String.toBase64(): String = this.toByteArray(Charset.defaultCharset()).toBase64()
private fun ByteArray.toBase64(): String = Base64.encodeToString(this, Base64.NO_WRAP)
private fun String.fromBase64ToString(): String = this.fromBase64ToByteArray().toString(Charset.defaultCharset())
private fun String.fromBase64ToByteArray(): ByteArray = Base64.decode(this, Base64.NO_WRAP)