/*
 * This file was generated by the Gradle 'init' task.
 *
 * The settings file is used to specify which projects to include in your build.
 *
 * Detailed information about configuring a multi-project build in Gradle can be found
 * in the user manual at https://docs.gradle.org/8.1.1/userguide/multi_project_builds.html
 */

rootProject.name = "Prepared"

pluginManagement {
	repositories {
		gradlePluginPortal()
		google()
	}

	includeBuild("gradle/conventions")
}

plugins {
	id("org.gradle.toolchains.foojay-resolver-convention") version "0.7.0"
}

include(
	"suite",
	"framework",

	"compat:compat-kotlinx-datetime",

	"runners:runner-kotlin-test",
)

buildCache {
	val username = System.getenv("GRADLE_BUILD_CACHE_CREDENTIALS")?.split(':')?.get(0)
	val password = System.getenv("GRADLE_BUILD_CACHE_CREDENTIALS")?.split(':')?.get(1)

	val mainBranch: String? = System.getenv("CI_DEFAULT_BRANCH")
	val currentBranch: String? = System.getenv("CI_COMMIT_REF_NAME")
	val runningForTag = System.getenv().containsKey("CI_COMMIT_TAG")

	remote<HttpBuildCache> {
		url = uri("https://gradle.opensavvy.dev/cache/")

		if (username != null && password != null) credentials {
			this.username = username
			this.password = password
		}

		isPush = (mainBranch != null && currentBranch != null && mainBranch == currentBranch) || runningForTag
	}
}
