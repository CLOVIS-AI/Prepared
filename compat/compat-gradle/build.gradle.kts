plugins {
	alias(opensavvyConventions.plugins.base)
	alias(opensavvyConventions.plugins.kotlin.library)
	alias(libs.plugins.testBalloon)
}

// groovy-all 2.5+ is POM-only (no JAR). gradle-test-kit declares it as a runtime POM dep,
// which breaks configuration cache resolution when tapmoc resolves JARs eagerly.
// Individual groovy modules are already present transitively, so excluding the BOM is safe.
// See https://github.com/GradleUp/tapmoc/issues/100
configurations.all {
	exclude(group = "org.codehaus.groovy", module = "groovy-all")
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
	homeUrl.set("https://prepared.opensavvy.dev/features/compat-gradle.html")

	license.set {
		name.set("Apache 2.0")
		url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
	}
}
