<p align="center">
  <a href="https://harmony.mobilejazz.com">
    <img src="https://raw.githubusercontent.com/mobilejazz/metadata/master/images/icons/harmony.svg" alt="MJ Harmony logo" width="80" height="80">
  </a>

<h3 align="center">Harmony Kotlin</h3>

  <p align="center">
    Harmony is a <em>framework</em> developed by <a href="https://mobilejazz.com">Mobile Jazz</a> that specifies best practices, software architectural patterns and other software development related guidelines.
    <br />
    <br />
    <a href="https://harmony.mobilejazz.com">Documentation</a>
    ·
    <a href="https://github.com/mobilejazz/harmony-typescript">TypeScript</a>
    ·
    <a href="https://github.com/mobilejazz/harmony-swift">Swift</a>
    ·
    <a href="https://github.com/mobilejazz/harmony-php">PHP</a>
  </p>
</p>

## Installation

Add the dependency for the multiplatform artifact (to be used on KMM project)
```groovy
dependencies {
  implementation 'com.mobilejazz:harmony-kotlin:5.0'
}
```
Or if you need to use it on a particular platform you can use the following artifacts
```groovy
  implementation 'com.mobilejazz:harmony-kotlin-android:5.0'
  implementation 'com.mobilejazz:harmony-kotlin-jvm:5.0'
  implementation 'com.mobilejazz:harmony-kotlin-iosx64:5.0'
  implementation 'com.mobilejazz:harmony-kotlin-iossimulatorarm64:5.0'
  implementation 'com.mobilejazz:harmony-kotlin-iosarm64:5.0'
```
**Android apps** targetting devices running **below API 26** need to use Android Gradle plugin 4.0 or newer and enable [core library desugaring](https://developer.android.com/studio/write/java8-support#library-desugaring). This is required due to the usage of [kotlinx-datetime](https://github.com/Kotlin/kotlinx-datetime)

## Migrating from 4.0 to 5.0

Apps migrating to version 5.0 must perform the following operations:
 - Follow Ktor 2.0 [migration guide](https://ktor.io/docs/migrating-2.html)
 - [Enable the new memory manager](https://github.com/JetBrains/kotlin/blob/master/kotlin-native/NEW_MM.md#switch-to-the-new-mm)

## Author

Mobile Jazz, info@mobilejazz.com

## API Reference

[https://harmony.mobilejazz.com/](https://harmony.mobilejazz.com/)

## License

Harmony is available under the Apache 2.0 license. See the LICENSE file for more info.
