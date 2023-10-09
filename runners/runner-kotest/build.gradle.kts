plugins {
	id("conventions.base")
	id("conventions.kotlin")
	id("conventions.library")
	alias(libs.plugins.kotest)
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
	name.set("Kotest support for Prepared")
	description.set("Execute Prepared test suites in projects that use Kotest")
	homeUrl.set("https://opensavvy.gitlab.io/prepared/documentation/runner-kotest/index.html")
}
