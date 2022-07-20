buildscript {
  repositories {
    gradlePluginPortal()
    google()
    mavenCentral()
  }
  dependencies {
    classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin_version")
    classpath("com.android.tools.build:gradle:$android_gradle_plugin_version")
    classpath("org.jetbrains.kotlin:kotlin-serialization:$kotlin_version")
  }
}

allprojects {
  repositories {
    mavenLocal()
    google()
    mavenCentral()
  }
}

plugins {
  id("org.jlleitschuh.gradle.ktlint") version ktlint_version
}

subprojects {
  apply {
    plugin("org.jlleitschuh.gradle.ktlint")
  }

  repositories {
    mavenCentral()
  }

  ktlint {
    verbose.set(true)
    outputToConsole.set(true)
    filter {
      exclude { entry ->
        entry.file.toString().contains("generated/")
      }
    }
  }
}

tasks.register("clean", Delete::class) {
  delete(rootProject.buildDir)
}
