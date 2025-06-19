plugins {
	alias(opensavvyConventions.plugins.base)
	alias(opensavvyConventions.plugins.kotlin.library)
	alias(libs.plugins.testBalloon)
}

kotlin {
	jvm()

	sourceSets.commonMain {
		dependencies {
			api(projects.suite)
			api(projects.compat.compatFilesystem)

			api(libs.gradle.testkit)
		}
	}

	sourceSets.commonTest {
		dependencies {
			implementation(projects.runners.runnerTestballoon)
		}
	}
}

library {
	name.set("Compatibility with Gradle TestKit")
	description.set("Test Gradle plugins using Prepared")
	homeUrl.set("https://opensavvy.gitlab.io/groundwork/prepared/api-docs/compat/compat-gradle/index.html")

	license.set {
		name.set("Apache 2.0")
		url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
	}
}
