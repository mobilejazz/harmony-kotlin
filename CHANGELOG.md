# CHANGELOG

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
  work
  
## 0.9.3 (30/07/2019)
- [DeviceStorageDataSource](/core/src/main/java/com/mobilejazz/harmony/kotlin/android/repository/datasource/srpreferences/DeviceStorageDataSource.kt)
now supports deleteAll clearing all keys with specified prefix or
clears it otherwise.