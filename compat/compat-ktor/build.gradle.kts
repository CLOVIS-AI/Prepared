plugins {
	alias(opensavvyConventions.plugins.base)
	alias(opensavvyConventions.plugins.kotlin.library)
}

kotlin {
	jvm {
		testRuns.named("test") {
			executionTask.configure {
				useJUnitPlatform()
			}
		}
	}
	linuxX64()
	iosArm64()
	iosSimulatorArm64()
	iosX64()

	val commonMain by sourceSets.getting {
		dependencies {
			api(projects.suite)
			api(libs.ktor.testHost)
		}
	}

	val commonTest by sourceSets.getting {
		dependencies {
			implementation(projects.runners.runnerKotest)
		}
	}
}

library {
	name.set("Compatibility with Ktor")
	description.set("Declare Ktor in-memory servers and clients as test fixtures")
	homeUrl.set("https://opensavvy.gitlab.io/prepared/api-docs/compat/compat-arrow/index.html")

	license.set {
		name.set("Apache 2.0")
		url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
	}
}
