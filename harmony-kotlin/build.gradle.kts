import com.android.build.gradle.internal.tasks.factory.dependsOn

// required the buildscript to add some of the plugins
buildscript {
  repositories {
    google()
    mavenCentral()
  }

  dependencies {
    classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${libs.versions.kotlin.get()}")
    classpath("org.jetbrains.kotlin:kotlin-serialization:${libs.versions.kotlin.get()}")
  }
}

@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
  kotlin("multiplatform")
  kotlin("plugin.serialization") version libs.versions.kotlin.get()
  id("com.android.library")
  alias(libs.plugins.sqldelight)
  `gradle-mvn-push` // on buildSrc/src/main/groovy/gradle-mvn-push.gradle
  alias(libs.plugins.mockmp)
  alias(libs.plugins.ksp) // <-- To avoid build to fail because mockmp is using a previous version of ksp
}
detekt {
  config = files("$rootDir/detekt.yml")
  // We have to set the source paths manually due to https://github.com/detekt/detekt/issues/3664
  source = files(
    "src/androidTest/kotlin",
    "src/commonMain/kotlin",
    "src/commonTest/kotlin",
    "src/iosMain/kotlin",
    "src/iosTest/kotlin",
    "src/jvmAndroidCommon/kotlin",
    "src/jvmMain/kotlin",
    "src/jvmTest/kotlin",
  )
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
        api(libs.coroutines.core)
        api(libs.bundles.serialization)
        api(libs.sqldelight.runtime)
        api(libs.bundles.ktor)
        api(libs.datetime)
        implementation(libs.uuidLib)
      }
    }
    val commonTest by getting {
      dependencies {
        implementation(kotlin("test-common"))
        implementation(kotlin("test-annotations-common"))
        implementation(libs.ktor.mock)
      }
    }

    // Common code shared between JVM and Android
    val jvmAndroidCommon = create("jvmAndroidCommon") {
      dependsOn(commonMain)
      dependencies {
        implementation(libs.gson)
      }
    }

    val jvmMain by getting {
      dependsOn(jvmAndroidCommon)
      dependencies {
        api(libs.sqldelight.sqliteDriver)
        api(libs.ktor.okhttp)
        api(libs.coroutines.test)
      }
    }

    val jvmTest by getting {
      dependencies {
        implementation(kotlin("test"))
        implementation(kotlin("test-junit"))
        implementation(libs.mockk)
      }
    }

    val androidMain by getting {
      dependsOn(jvmAndroidCommon)
      dependencies {
        api(libs.ktor.android)
        api(libs.coroutines.android)
        api(libs.coroutines.test)

        // Android Support
        api(libs.appCompat)
        api(libs.coordinatorLayout)

        api(libs.sqldelight.androidDriver)

        api(libs.bugfender)
      }
    }

    val androidTest by getting {
      dependencies {
        implementation(kotlin("test-junit"))
        implementation(libs.junit)

        implementation(libs.kotlinTest)
        implementation(libs.kotlinTestJunit)

        implementation(libs.ktor.mockJvm)

        // Instrumentation tests
        implementation(libs.androidTest.core)
        implementation(libs.androidTest.runner)
        implementation(libs.androidTest.rules)
        implementation(libs.androidTest.junitKtx)
        implementation(libs.roboelectric)
        implementation(libs.androidTest.lifecycleRuntime)
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
        api(libs.bundles.serialization)
        api(libs.sqldelight.nativeDriver)
        api(libs.ktor.ios)
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

apply(from = rootProject.file("git-hooks.gradle.kts"))
