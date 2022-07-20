import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
  kotlin("multiplatform")
  kotlin("native.cocoapods")
  id("com.android.library")
  kotlin("plugin.serialization")
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
    ios.deploymentTarget = "14.1"
    framework {
      baseName = "SampleCore"
    }
    podfile = project.file("../sample-ios/Podfile")
  }

  sourceSets {
    val commonMain by getting {
      dependencies {
        api(project(":harmony-kotlin"))
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
        implementation("io.ktor:ktor-client-okhttp:$ktor_version")
      }
    }
    val androidTest by getting {
      dependencies {
        implementation(project(":harmony-kotlin"))
        implementation(kotlin("test-junit"))
        implementation("junit:junit:$junit_version")
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
        implementation("io.ktor:ktor-client-ios:$ktor_version")
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
}

android {
  compileSdkVersion(31)
  sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
  defaultConfig {
    minSdkVersion(21)
    targetSdkVersion(31)
  }
}
