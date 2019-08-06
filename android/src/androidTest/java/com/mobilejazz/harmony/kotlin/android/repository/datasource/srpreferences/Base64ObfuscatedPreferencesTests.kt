package com.mobilejazz.harmony.kotlin.android.repository.datasource.srpreferences

import android.content.Context
import android.support.test.runner.AndroidJUnit4
import com.mobilejazz.harmony.kotlin.android.InstrumentationTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class Base64ObfuscatedPreferencesTests : InstrumentationTest() {

  private val preferences = Base64ObfuscatedPreferences(appContext, "base64.obfuscated.prefs.test")
  private val clearTextPreferences = appContext.getSharedPreferences("base64.obfuscated.prefs.test", Context.MODE_PRIVATE)

  private val anyKey = "any.key"

  @Before
  fun setUp() {
    preferences.edit().clear().commit()

  }

  @Test
  fun shouldReturnDeobfuscatedBoolean_WhenGettingIt() {
    // Given
    val expected = false
    preferences.edit().putBoolean(anyKey, expected).commit()

    // When
    val actual = preferences.getBoolean(anyKey, true)

    // Then
    Assert.assertEquals(expected, actual)
  }

  @Test
  fun shouldReturnDeobfuscatedInt_WhenGettingIt() {
    // Given
    val expected = 42
    preferences.edit().putInt(anyKey, expected).commit()

    // When
    val actual = preferences.getInt(anyKey, 24)

    // Then
    Assert.assertEquals(expected, actual)
  }

  @Test
  fun shouldReturnDeobfuscatedFloat_WhenGettingIt() {
    // Given
    val expected = 42.66F
    preferences.edit().putFloat(anyKey, expected).commit()

    // When
    val actual = preferences.getFloat(anyKey, 24.66F)

    // Then
    Assert.assertEquals(expected, actual)
  }

  @Test
  fun shouldReturnDeobfuscatedLong_WhenGettingIt() {
    // Given
    val expected = 42L
    preferences.edit().putLong(anyKey, expected).commit()

    // When
    val actual = preferences.getLong(anyKey, 24L)

    // Then
    Assert.assertEquals(expected, actual)
  }

  @Test
  fun shouldReturnDeobfuscatedString_WhenGettingIt() {
    // Given
    val expected = "Any string"
    preferences.edit().putString(anyKey, expected).commit()

    // When
    val actual = preferences.getString(anyKey, "Any other string")

    // Then
    Assert.assertEquals(expected, actual)
  }

  @Test
  fun shouldReturnDeobfuscatedStringSet_WhenGettingIt() {
    // Given
    val expected = setOf("Any string 1", "Any string 2")
    preferences.edit().putStringSet(anyKey, expected).commit()

    // When
    val actual = preferences.getStringSet(anyKey, setOf("Any other string 1", "Any other string 2"))

    // Then
    Assert.assertEquals(expected, actual)
  }

  @Test
  fun shouldReturnDeobfuscatedValuesAsStringOrStringSet_WhenGettingAll() {
    // Given
    val expectedBoolean = false
    val expectedFloat = 42.66F
    val expectedString = "Any String"
    val expectedSet = setOf("Any string 1", "Any string 2")
    preferences.edit()
        .putBoolean("boolean.key", expectedBoolean)
        .putFloat("float.key", expectedFloat)
        .putString("string.key", expectedString)
        .putStringSet("set.key", expectedSet).commit()

    val expectedMap = mapOf(
        Pair("boolean.key", expectedBoolean.toString()),
        Pair("float.key", expectedFloat.toString()),
        Pair("string.key", expectedString),
        Pair("set.key", expectedSet)
    )

    // When
    val actual = preferences.all

    // Then
    Assert.assertEquals(expectedMap, actual)
  }

  // region Testing same preferences being used with clear text
  @Test
  fun shouldReturnFloatDefaultValue_WhenGettingIt_GivenKeyAndValueStoredAsClearText() {
    // Given
    clearTextPreferences.edit().putFloat(anyKey, 42.66F).commit()

    // When
    val expected = 24.66F
    val actual = preferences.getFloat(anyKey, expected)

    // Then
    Assert.assertEquals(expected, actual)
  }

  @Test
  fun shouldReturnStringDefaultValue_WhenGettingIt_GivenKeyAndValueStoredAsClearText() {
    // Given
    clearTextPreferences.edit().putString(anyKey, "Any string").commit()

    // When
    val expected = "Any other string"
    val actual = preferences.getString(anyKey, expected)

    // Then
    Assert.assertEquals(expected, actual)
  }

  @Test
  fun shouldReturnStringSetDefaultValue_WhenGettingIt_GivenKeyAndValueStoredAsClearText() {
    // Given
    clearTextPreferences.edit().putStringSet(anyKey, setOf("Any string 1", "Any string 2")).commit()

    // When
    val expected = setOf("Any other string 1", "Any other string 2")
    val actual = preferences.getStringSet(anyKey, expected)

    // Then
    Assert.assertEquals(expected, actual)
  }

  @Test
  fun shouldReturnOnlyPreviouslyObfuscatedValues_WhenGettingAll_GivenSomeKeysAndValuesStoredAsClearText() {
    // Given
    clearTextPreferences.edit()
        .putBoolean("boolean.key", false)
        .putFloat("float.key", 42.66F)
        .putString("string.key", "Any String")
        .putStringSet("set.key", setOf("Any string 1", "Any string 2")).commit()

    preferences.edit()
        .putLong("long.key", 300L).commit()

    // When
    val actual = preferences.all

    // Then
    Assert.assertEquals(actual.size, 1)
    Assert.assertEquals(actual["long.key"], 300L.toString())
  }

  // endregion

}