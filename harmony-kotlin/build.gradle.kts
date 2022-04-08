import com.android.build.gradle.internal.tasks.factory.dependsOn

// required the buildscript to add some of the plugins
buildscript {
  repositories {
    google()
    mavenCentral()
  }

  dependencies {
    classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin_version")
    classpath("org.jetbrains.kotlin:kotlin-serialization:$kotlin_version")
  }
}

plugins {
  kotlin("multiplatform")
  kotlin("plugin.serialization")
  id("com.squareup.sqldelight") version sql_delight_version
  id("com.android.library")
  id("org.kodein.mock.mockmp") version mockmp_version
  `gradle-mvn-push` // on buildSrc/src/main/groovy/gradle-mvn-push.gradle
}

mockmp {
  usesHelper = true
}

kotlin {
  jvm()
  android()
  iosX64()
  iosArm64()
  iosSimulatorArm64() // sure all ios dependencies support this target

  android {
    publishLibraryVariants("release")
  }

  sourceSets {
    all {
      languageSettings.apply {
        optIn("kotlin.RequiresOptIn")
        optIn("kotlinx.coroutines.ExperimentalCoroutinesApi")
        optIn("kotlinx.serialization.ExperimentalSerializationApi")
      }
    }

    val commonMain by getting {
      dependencies {
        // coroutines
        api("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutines_version") {
          version {
            strictly("$coroutines_version")
          }
        }

        // serialization library
        api("org.jetbrains.kotlinx:kotlinx-serialization-core:$serialization_version")
        api("org.jetbrains.kotlinx:kotlinx-serialization-cbor:$serialization_version")

        // SQL Delight
        api("com.squareup.sqldelight:runtime:$sql_delight_version")

        // http client library
        api("io.ktor:ktor-client-core:$ktor_version")
        api("io.ktor:ktor-client-json:$ktor_version")
        api("io.ktor:ktor-client-logging:$ktor_version")
        api("io.ktor:ktor-client-serialization:$ktor_version")

        // date library
        api("org.jetbrains.kotlinx:kotlinx-datetime:$kotlinx_datetime_version")

        // UUID library
        implementation("com.benasher44:uuid:$uuid_lib_version")
      }
    }
    val commonTest by getting {
      dependencies {
        implementation(kotlin("test-common"))
        implementation(kotlin("test-annotations-common"))
        implementation("io.ktor:ktor-client-mock:$ktor_version")
      }
    }

    val jvmMain by getting {
      dependencies {
        implementation("com.google.code.gson:gson:2.8.7")

        api("com.squareup.sqldelight:sqlite-driver:$sql_delight_version")

        // ktor
        api("io.ktor:ktor-client-okhttp:$ktor_version")

        api("org.jetbrains.kotlinx:kotlinx-coroutines-test:$coroutines_version")
      }
    }

    val jvmTest by getting {
      dependencies {
        implementation(kotlin("test"))
        implementation(kotlin("test-junit"))
      }
    }

    val androidMain by getting {
      dependencies {
        api("io.ktor:ktor-client-android:$ktor_version")
        api("org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutines_version")
        api("org.jetbrains.kotlinx:kotlinx-coroutines-test:$coroutines_version")

        // Android Support
        api("androidx.appcompat:appcompat:1.4.0")
        implementation("androidx.coordinatorlayout:coordinatorlayout:1.1.0")

        // SQL Delight
        api("com.squareup.sqldelight:android-driver:$sql_delight_version")

        // Bugfender
        api("com.bugfender.sdk:android:3.0.10")
      }
    }

    val androidTest by getting {
      dependencies {
        implementation(kotlin("test-junit"))
        implementation("junit:junit:4.13.2")

        implementation("org.jetbrains.kotlin:kotlin-test:$kotlin_version")
        implementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")

        implementation("io.ktor:ktor-client-mock-jvm:$ktor_version")

        // Instrumentation tests
        implementation("androidx.test:core:1.4.0")
        implementation("androidx.test:runner:1.4.0")
        implementation("androidx.test:rules:1.4.0")
        implementation("androidx.test.ext:junit-ktx:1.1.3")
        implementation("org.robolectric:robolectric:4.7.1")
        implementation("androidx.lifecycle:lifecycle-runtime-testing:2.4.1")
      }
    }
    val iosX64Main by getting
    val iosArm64Main by getting
    val iosSimulatorArm64Main by getting
    val iosMain by creating {
      dependsOn(commonMain)
      iosX64Main.dependsOn(this)
      iosArm64Main.dependsOn(this)
      iosSimulatorArm64Main.dependsOn(this)

      dependencies {
        api("org.jetbrains.kotlinx:kotlinx-serialization-core:$serialization_version")
        api("org.jetbrains.kotlinx:kotlinx-serialization-cbor:$serialization_version")

        // sqldelight
        api("com.squareup.sqldelight:native-driver:$sql_delight_version")
        api("io.ktor:ktor-client-ios:$ktor_version")
      }
    }
    val iosX64Test by getting
    val iosArm64Test by getting
    val iosSimulatorArm64Test by getting
    val iosTest by creating {
      dependsOn(commonTest)
      iosX64Test.dependsOn(this)
      iosArm64Test.dependsOn(this)
      iosSimulatorArm64Test.dependsOn(this)
    }
  }

  tasks.preBuild {
    dependsOn(tasks.ktlintFormat)
  }
}

sqldelight {
  database("CacheDatabase") {
    packageName = "com.harmony.kotlin.data.datasource.database"
    sourceFolders = listOf("sqldelight")
  }
}

android {

  compileSdk = android_target_sdk_version
  sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
  defaultConfig {
    minSdk = android_min_sdk_version
    targetSdk = android_target_sdk_version
  }

  testOptions {
    unitTests.isIncludeAndroidResources = true
  }
}

tasks.check.dependsOn(tasks.ktlintCheck)
