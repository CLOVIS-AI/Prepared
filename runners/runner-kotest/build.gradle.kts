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
	js(IR) {
		browser {
			testTask {
				useMocha {
					timeout = "1 minute"
				}
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

			api(libs.kotest.engine)
		}
	}

	val jvmMain by sourceSets.getting {
		dependencies {
			api(libs.kotest.runner.junit5)
		}
	}
}

tasks.withType(Test::class) {
	testLogging {
		events("skipped", "failed", "passed")
	}
}

library {
	name.set("Execute with Kotest")
	description.set("Execute Prepared test suites in projects that use Kotest")
	homeUrl.set("https://opensavvy.gitlab.io/prepared/api-docs/runners/runner-kotest/index.html")

	license.set {
		name.set("Apache 2.0")
		url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
	}
}
