import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
  kotlin("multiplatform")
  kotlin("native.cocoapods")
  id("com.android.library")
  kotlin("plugin.serialization")
}

version = "1.0"

kotlin {
  android()

  val iosTarget: (String, KotlinNativeTarget.() -> Unit) -> KotlinNativeTarget = when {
    System.getenv("SDK_NAME")?.startsWith("iphoneos") == true -> ::iosArm64
    System.getenv("NATIVE_ARCH")?.startsWith("arm") == true -> ::iosSimulatorArm64
    else -> ::iosX64
  }

  iosTarget("ios") {}

  cocoapods {
    summary = "Some description for the Shared Module"
    homepage = "Link to the Shared Module homepage"
    ios.deploymentTarget = "14.1"
    framework {
      baseName = "core"
    }
    podfile = project.file("../sample-ios/Podfile")
  }

  sourceSets {
    val commonMain by getting {
      dependencies {
        implementation(project(":harmony-kotlin"))
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
        implementation("io.ktor:ktor-client-okhttp:1.6.4")
      }
    }
    val androidTest by getting {
      dependencies {
        implementation(project(":harmony-kotlin"))
        implementation(kotlin("test-junit"))
        implementation("junit:junit:4.13.2")
      }
    }
    val iosMain by getting
    val iosTest by getting
  }
}

android {
  compileSdkVersion(31)
  sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
  defaultConfig {
    minSdkVersion(21)
    targetSdkVersion(31)
  }
}
