buildscript {
  repositories {
    gradlePluginPortal()
    google()
    mavenCentral()
  }
  dependencies {
    classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin_version")
    classpath("com.android.tools.build:gradle:7.0.4")
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
  id("org.jlleitschuh.gradle.ktlint") version "10.2.0"
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
      exclude("**/generated/**")
    }
  }
}

tasks.register("clean", Delete::class) {
  delete(rootProject.buildDir)
}
