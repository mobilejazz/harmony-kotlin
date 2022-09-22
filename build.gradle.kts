import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask

buildscript {
  repositories {
    gradlePluginPortal()
    google()
    mavenCentral()
  }
  dependencies {
    classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${libs.versions.kotlin.get()}")
    classpath("com.android.tools.build:gradle:${libs.versions.androidGradlePlugin.get()}")
  }
}

allprojects {
  repositories {
    mavenLocal()
    google()
    mavenCentral()
  }
}

@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
  alias(libs.plugins.ktlint)
  alias(libs.plugins.detekt)
  alias(libs.plugins.taskTree)

  // version-catalog update configuration
  alias(libs.plugins.gradleVersions)
  alias(libs.plugins.versionCatalogUpdate)
}

subprojects {
  apply {
    plugin("org.jlleitschuh.gradle.ktlint")
    plugin("io.gitlab.arturbosch.detekt")
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
  detekt {
    config = files("$rootDir/detekt.yml")
  }
}

tasks.register("clean", Delete::class) {
  delete(rootProject.buildDir)
}

//region version-catalog update configuration
fun isNonStable(version: String): Boolean {
  val stableKeyword = listOf("RELEASE", "FINAL", "GA").any { version.toUpperCase().contains(it) }
  val regex = "^[0-9,.v-]+(-r)?$".toRegex()
  val isStable = stableKeyword || regex.matches(version)
  return isStable.not()
}

// https://github.com/ben-manes/gradle-versions-plugin
tasks.withType<DependencyUpdatesTask> {
  rejectVersionIf {
    isNonStable(candidate.version) && !isNonStable(currentVersion)
  }
}

versionCatalogUpdate {
  // sort the catalog by key (default is true)
  sortByKey.set(false)
  keep {
    // keep versions without any library or plugin reference
    keepUnusedVersions.set(true)
    // keep all libraries that aren't used in the project
    keepUnusedLibraries.set(false)
    // keep all plugins that aren't used in the project
    keepUnusedPlugins.set(false)
  }
}
//endregion version-catalog update configuration
