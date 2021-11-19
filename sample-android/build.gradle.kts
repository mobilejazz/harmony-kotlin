plugins {
  id("com.android.application")
  kotlin("android")
}

dependencies {
  implementation(project(":sample-core"))
  implementation("com.google.android.material:material:1.4.0")
  implementation("androidx.appcompat:appcompat:1.4.0")
  implementation("androidx.constraintlayout:constraintlayout:2.1.2")
  implementation("com.mobilejazz:harmony-kotlin-android:2.0")
}

android {
  compileSdk = 31
  defaultConfig {
    applicationId = "com.mobilejazz.kmmsample.android"
    minSdk = 26
    targetSdk = 31
    versionCode = 1
    versionName = "1.0"
  }
  buildTypes {
    getByName("release") {
      isMinifyEnabled = false
    }
  }
  buildFeatures {
    viewBinding = true
  }
}
