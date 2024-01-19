import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.buildFeatures.perfmon
import jetbrains.buildServer.configs.kotlin.buildSteps.maven
import jetbrains.buildServer.configs.kotlin.triggers.vcs
import jetbrains.buildServer.configs.kotlin.vcs.GitVcsRoot

/*
The settings script is an entry point for defining a TeamCity
project hierarchy. The script should contain a single call to the
project() function with a Project instance or an init function as
an argument.

VcsRoots, BuildTypes, Templates, and subprojects can be
registered inside the project using the vcsRoot(), buildType(),
template(), and subProject() methods respectively.

To debug settings scripts in command-line, run the

    mvnDebug org.jetbrains.teamcity:teamcity-configs-maven-plugin:generate

command and attach your debugger to the port 8000.

To debug in IntelliJ Idea, open the 'Maven Projects' tool window (View
-> Tool Windows -> Maven Projects), find the generate task node
(Plugins -> teamcity-configs -> teamcity-configs:generate), the
'Debug' option is available in the context menu for the task.
*/

version = "2023.11"

project {

    vcsRoot(HttpsGithubComAagrebeshkovExampleTeamcityGitRefsHeadsMain)
    vcsRoot(HttpsGithubComAragastmatbExampleTeamcityGitRefsHeadsMaster)

    buildType(Build)
    buildType(Build1)

    template(Export)
}

object Build : BuildType({
    name = "Build"

    vcs {
        root(HttpsGithubComAragastmatbExampleTeamcityGitRefsHeadsMaster)
    }

    steps {
        maven {
            id = "Maven2"
            goals = "clean test"
            runnerArgs = "-Dmaven.test.failure.ignore=true"
        }
    }

    triggers {
        vcs {
        }
    }

    features {
        perfmon {
        }
    }
})

object Build1 : BuildType({
    templates(Export)
    name = "Build (1)"

    steps {
        maven {
            name = "Maven2_2"
            id = "Maven2_2"

            conditions {
                doesNotContain("teamcity.build.branch", "main")
            }
            goals = "clean test"
            runnerArgs = "-Dmaven.test.failure.ignore=true"
            userSettingsSelection = "settings.xml"
        }
        stepsOrder = arrayListOf("Maven2", "Maven2_2", "Maven2_1")
    }
})

object Export : Template({
    name = "export"

    vcs {
        root(HttpsGithubComAagrebeshkovExampleTeamcityGitRefsHeadsMain)
    }

    steps {
        maven {
            id = "Maven2"

            conditions {
                contains("teamcity.build.branch", "main")
            }
            goals = "clean deploy"
            runnerArgs = "-Dmaven.test.failure.ignore=true"
            userSettingsSelection = "settings.xml"
        }
    }

    triggers {
        vcs {
            id = "TRIGGER_2"
        }
    }

    features {
        perfmon {
            id = "perfmon"
        }
    }
})

object HttpsGithubComAagrebeshkovExampleTeamcityGitRefsHeadsMain : GitVcsRoot({
    name = "https://github.com/aagrebeshkov/example-teamcity.git#refs/heads/main"
    url = "https://github.com/aagrebeshkov/example-teamcity.git"
    branch = "refs/heads/main"
    branchSpec = "refs/heads/*"
})

object HttpsGithubComAragastmatbExampleTeamcityGitRefsHeadsMaster : GitVcsRoot({
    name = "https://github.com/aragastmatb/example-teamcity.git#refs/heads/master"
    url = "https://github.com/aragastmatb/example-teamcity.git"
    branch = "refs/heads/master"
    branchSpec = "refs/heads/*"
})
