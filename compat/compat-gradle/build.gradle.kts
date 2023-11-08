plugins {
	id("conventions.base")
	id("conventions.kotlin")
	id("conventions.library")
}

kotlin {
	jvm()

	val commonMain by sourceSets.getting {
		dependencies {
			api(projects.suite)
			api(projects.compat.compatFilesystem)

			api(libs.gradle.testkit)
		}
	}

	val commonTest by sourceSets.getting {
		dependencies {
			implementation(projects.framework)
		}
	}
}

library {
	name.set("Compatibility with Gradle TestKit")
	description.set("Test Gradle plugins using Prepared")
	homeUrl.set("https://opensavvy.gitlab.io/prepared/api-docs/compat/compat-gradle/index.html")
}
