import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
  kotlin("multiplatform")
  kotlin("native.cocoapods")
  id("com.android.library")
  kotlin("plugin.serialization") version libs.versions.kotlin.get()
}

version = "1.0"

kotlin {
  targets.withType<KotlinNativeTarget> {
    binaries.withType<org.jetbrains.kotlin.gradle.plugin.mpp.Framework> {
      isStatic = false
      linkerOpts.add("-lsqlite3")
      export(project(":harmony-kotlin"))
    }
  }

  android()

  iosX64()
  iosArm64()
  iosSimulatorArm64()

  cocoapods {
    summary = "Some description for the Shared Module"
    homepage = "Link to the Shared Module homepage"
    ios.deploymentTarget = "14.0"
    framework {
      baseName = "SampleCore"
    }
    podfile = project.file("../sample-ios/Podfile")
  }

  sourceSets {
    val commonMain by getting {
      dependencies {
        api(project(":harmony-kotlin"))
//        api(libs.serialization.json)
      }
    }
    val commonTest by getting {
      dependencies {
        implementation(kotlin("test-common"))
        implementation(kotlin("test-annotations-common"))
      }
    }
    val androidMain by getting {
      dependencies {
        api(project(":harmony-kotlin"))
        implementation(libs.ktor.okhttp)
      }
    }
    val androidTest by getting {
      dependencies {
        implementation(project(":harmony-kotlin"))
        implementation(kotlin("test-junit"))
        implementation(libs.junit)
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
        implementation(libs.ktor.ios)
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

android {
  compileSdk = android_target_sdk_version
  sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
  defaultConfig {
    minSdk = android_min_sdk_version
    targetSdk = android_target_sdk_version
  }
}
