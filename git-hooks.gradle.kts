tasks {
  register<Copy>("copyGitHooks") {
    description = "Copies the git hooks from scripts/git-hooks to the .git folder."
    group = "git hooks"
    from("$rootDir/scripts/git-hooks/") {
      include("**/*.sh")
      rename("(.*).sh", "$1")
    }
    into("$rootDir/.git/hooks")
  }

  register<Exec>("installGitHooks") {
    description = "Installs the pre-commit git hooks from scripts/git-hooks."
    group = "git hooks"
    workingDir(rootDir)
    commandLine("chmod")
    args("-R", "+x", "$rootDir/.git/hooks/")
    dependsOn(named("copyGitHooks"))
    doLast {
      logger.info("Git hooks installed successfully.")
    }
  }

  register<Delete>("deleteGitHooks") {
    description = "Delete the pre-commit git hooks."
    group = "git hooks"
    delete(fileTree("$rootDir/.git/hooks/"))
  }

  afterEvaluate {
    tasks["assemble"].dependsOn(tasks.named("installGitHooks"))
  }
}
