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

configurations.all {
  resolutionStrategy.eachDependency {
    if (requested.version == "default") {
      val version = findDefaultVersionInCatalog(requested.group, requested.name)
      version?.also {
        useVersion(it.version)
        because(it.because)
      }
    }
  }
}

data class DefaultVersion(val version: String, val because: String)

fun findDefaultVersionInCatalog(group: String, name: String): DefaultVersion? {
  return if (group == "org.jetbrains.kotlinx" && name.startsWith("kotlinx-coroutines")) {
    DefaultVersion(version = coroutines_version, because = "Is needed in Harmony")
  } else {
    null
  }
}
