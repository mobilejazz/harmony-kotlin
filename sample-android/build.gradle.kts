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
