plugins {
  id("com.android.application")
  kotlin("android")
}

dependencies {
  implementation(project(":sample-core"))

  implementation("com.google.android.material:material:$material_version")
  implementation("androidx.appcompat:appcompat:$app_compat_version")
  implementation("androidx.constraintlayout:constraintlayout:$constraint_layout_version")
}

android {
  packagingOptions {
    resources.excludes.add("META-INF/licenses/**")
    resources.excludes.add("META-INF/AL2.0")
    resources.excludes.add("META-INF/LGPL2.1")
  }

  compileSdk = android_target_sdk_version
  defaultConfig {
    applicationId = "com.mobilejazz.kmmsample.android"
    minSdk = android_min_sdk_version
    targetSdk = android_target_sdk_version
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
