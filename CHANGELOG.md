# CHANGELOG

## 1.0.1 (25/10/2019)
- Exact same version as 1.0. It is created to solve a issue with JitPack
that currently is using a older invalid build with version 1.0 which 
can't be removed

## 1.0 (25/10/2019)
- Migration to AndroidX
- Improved CacheRepository and CacheSyncOperation to allow returning a
  non valid object when MainOperation fails
  [PR #9](https://github.com/mobilejazz/harmony-kotlin/pull/9)
- Changes on TimestampValidationStrategy usage.
- Removed DataSourceValidator and DataSourceVastraValidator

## 0.9.5 (09/09/2019)
- Make DataNotFoundException open to be able to use custom Exceptions with
CacheRepository

## 0.9.4 (06/08/2019)
- Added [Base64ObfuscatedSharedPreferences](/android/src/main/java/com/mobilejazz/harmony/kotlin/android/repository/datasource/srpreferences/Base64ObfuscatedPreferences.kt) that obfuscate stored key/values using base 64 enconding
- Added String and ByteArray extensions [PR #6](https://github.com/mobilejazz/harmony-kotlin/pull/6)
- Changed [DatabaseStorageDataSource] to use AndroidX SQLite interfaces to allow the usage of different implementations of the database [PR #3](https://github.com/mobilejazz/harmony-kotlin/pull/3)

## 0.9.3 (30/07/2019)
- [DeviceStorageDataSource](/core/src/main/java/com/mobilejazz/harmony/kotlin/android/repository/datasource/srpreferences/DeviceStorageDataSource.kt)
now supports deleteAll clearing all keys with specified prefix or
clears it otherwise.

## 0.9.2 (24/07/2019)
- Fix on [ListenableFutureExt.blockError](/core/src/main/java/com/mobilejazz/harmony/kotlin/core/threading/extensions/ListenableFutureExt.kt) to avoid `kotlin.KotlinNullPointerException` when an Exception is thrown in the Future

## 0.9.1 (23/07/2019)
- Added
  [Localized](/core/src/main/java/com/mobilejazz/harmony/kotlin/core/helpers/Localized.kt)
  interface in core in order to abstract string localization
- Added
  [LocalizedString](/android/src/main/java/com/mobilejazz/harmony/kotlin/android/helpers/LocalizedStrings.kt)
  in android module, an implementation of
  [Localized](/core/src/main/java/com/mobilejazz/harmony/kotlin/core/helpers/Localized.kt)
  interface, that localizes String in android platform.
- Updated sample code to demonstrate ho
  [LocalizedString](/android/src/main/java/com/mobilejazz/harmony/kotlin/android/helpers/LocalizedStrings.kt)
  workº1
