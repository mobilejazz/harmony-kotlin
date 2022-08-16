plugins {
  id("com.android.application")
  id("org.jetbrains.kotlin.android")
  // Used for Compose Destinations(https://github.com/raamcosta/compose-destinations)
  id("com.google.devtools.ksp") version ksp_version
}

android {
  compileSdk = android_target_sdk_version

  defaultConfig {
    applicationId = "com.mobilejazz.kmmsample.mvi"
    minSdk = android_min_sdk_version
    targetSdk = android_target_sdk_version
    versionCode = 1
    versionName = "1.0"

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    vectorDrawables {
      useSupportLibrary = true
    }
  }

  buildTypes {
    release {
      isMinifyEnabled = false
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
    }
  }
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
  }
  kotlinOptions {
    jvmTarget = "1.8"
  }
  buildFeatures {
    compose = true
  }
  composeOptions {
    kotlinCompilerExtensionVersion = compose_compiler_version
  }
  packagingOptions {
    resources {
      excludes += "/META-INF/{AL2.0,LGPL2.1}"
    }
  }

  applicationVariants.all {
    kotlin.sourceSets {
      getByName(name) {
        kotlin.srcDir("build/generated/ksp/$name/kotlin")
      }
    }
  }
}

dependencies {
  implementation(project(":sample-core"))

  implementation("androidx.core:core-ktx:$core_ktx_version")
  implementation("androidx.compose.ui:ui:$compose_version")
  implementation("androidx.compose.material:material:$compose_version")
  implementation("androidx.compose.ui:ui-tooling-preview:$compose_version")
  implementation("androidx.lifecycle:lifecycle-runtime-ktx:$lifecycle_version")
  implementation("androidx.lifecycle:lifecycle-viewmodel-compose:$lifecycle_version")
  implementation("androidx.activity:activity-compose:$activity_compose_version")
  implementation("io.github.raamcosta.compose-destinations:core:$compose_destinations_version")
  ksp("io.github.raamcosta.compose-destinations:ksp:$compose_destinations_version")
  testImplementation("junit:junit:$junit_version")
  androidTestImplementation("androidx.test.ext:junit:$androidx_junit_ktx_version")
  androidTestImplementation("androidx.compose.ui:ui-test-junit4:$compose_version")
  debugImplementation("androidx.compose.ui:ui-tooling:$compose_version")
  debugImplementation("androidx.compose.ui:ui-test-manifest:$compose_version")
}
