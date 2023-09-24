package conventions

import gradle.kotlin.dsl.accessors._b6bea14fb88fd11e46d6fb1ebe601eab.ext
import gradle.kotlin.dsl.accessors._b6bea14fb88fd11e46d6fb1ebe601eab.publishing
import gradle.kotlin.dsl.accessors._b6bea14fb88fd11e46d6fb1ebe601eab.signing

plugins {
	id("maven-publish")
	id("signing")
}

// Global configuration for the repository
// Remember to change this when using in your own project!

fun MavenPom.buildConfiguration() {
	licenses {
		license {
			name.set("Apache 2.0")
			url.set("https://www.apache.org/licenses/LICENSE-2.0")
		}
	}

	developers {
		developer {
			id.set("clovis-ai")
			name.set("Ivan “CLOVIS” Canet")
			email.set("ivan.canet@gmail.com")
		}
	}

	scm {
		url.set("https://gitlab.com/opensavvy/playgrounds/gradle")
	}
}

// region Metadata

interface LibraryExtension {
	val name: Property<String>
	val description: Property<String>
	val homeUrl: Property<String>
}

val config = extensions.create<LibraryExtension>("library")

// endregion
// region GitLab Maven Registry

// When running in GitLab CI, uses the auto-created CI variables to configure the GitLab Maven Registry.
// For more information on the variables and their values, see:
// - https://docs.gitlab.com/ee/user/packages/maven_repository/
// - https://docs.gitlab.com/ee/ci/variables/predefined_variables.html
publishing {
	repositories {
		val projectId = System.getenv("CI_PROJECT_ID") ?: return@repositories
		val token = System.getenv("CI_JOB_TOKEN") ?: return@repositories
		val api = System.getenv("CI_API_V4_URL") ?: return@repositories

		maven {
			name = "GitLab"
			url = uri("$api/projects/$projectId/packages/maven")

			credentials(HttpHeaderCredentials::class.java) {
				name = "Job-Token"
				value = token
			}

			authentication {
				create<HttpHeaderAuthentication>("header")
			}
		}
	}
}

// endregion
// region Maven Central

val fakeJavadocJar by tasks.registering(Jar::class) {
	description = "Fake documentation JAR for MavenCentral"
	group = "publishing"

	archiveClassifier.set("javadoc")
}

publishing {
	publications.withType<MavenPublication> {
		artifact(fakeJavadocJar.get())

		pom {
			name.set(config.name)
			description.set(config.description)
			url.set(config.homeUrl)

			buildConfiguration()
		}
	}
}

run {
	ext["signing.keyId"] = System.getenv("SIGNING_KEY_ID") ?: return@run
	ext["signing.password"] = System.getenv("SIGNING_PASSWORD") ?: return@run
	ext["signing.secretKeyRingFile"] = System.getenv("SIGNING_KEY_RING") ?: return@run

	// Workaround for https://youtrack.jetbrains.com/issue/KT-61858
	val signingTasks = tasks.withType(Sign::class)
	tasks.withType(AbstractPublishToMaven::class).configureEach {
		dependsOn(signingTasks)
	}

	signing {
		sign(publishing.publications)
	}
}

//endregion
