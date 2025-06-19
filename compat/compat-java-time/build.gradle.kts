plugins {
	alias(opensavvyConventions.plugins.base)
	alias(opensavvyConventions.plugins.kotlin.library)
	alias(libs.plugins.testBalloon)
}

kotlin {
	jvm()

	sourceSets.jvmMain {
		dependencies {
			api(projects.suite)
		}
	}

	sourceSets.jvmTest {
		dependencies {
			implementation(projects.runners.runnerTestballoon)
		}
	}
}

library {
	name.set("Compatibility with java.time")
	description.set("Control the passing of time in Prepared tests using objects and methods from java.time, including Clock and Instant")
	homeUrl.set("https://opensavvy.gitlab.io/groundwork/prepared/api-docs/compat/compat-java-time/index.html")

	license.set {
		name.set("Apache 2.0")
		url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
	}
}

kotlin {
	jvmToolchain(11)
}
