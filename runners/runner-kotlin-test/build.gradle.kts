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
	js {
		nodejs()
		browser {
			testTask {
				useMocha {
					timeout = "1 minute"
				}
			}
		}
	}

	val commonMain by sourceSets.getting {
		dependencies {
			api(projects.suite)

			api(opensavvyConventions.aligned.kotlin.test.common)
			api(opensavvyConventions.aligned.kotlin.test.annotations)
		}
	}

	val commonTest by sourceSets.getting {
		dependencies {
			implementation(projects.compat.compatKotlinxDatetime)
		}
	}

	val jvmMain by sourceSets.getting {
		dependencies {
			api(opensavvyConventions.aligned.kotlin.test.junit5)
		}
	}

	val jsMain by sourceSets.getting {
		dependencies {
			api(opensavvyConventions.aligned.kotlin.test.js)
		}
	}
}

tasks.withType(Test::class) {
	testLogging {
		events("skipped", "failed", "passed")
	}
}

library {
	name.set("Execute with Kotlin-test")
	description.set("Execute Prepared test suites in projects that use kotlin-test")
	homeUrl.set("https://opensavvy.gitlab.io/prepared/api-docs/runners/runner-kotlin-test/index.html")

	license.set {
		name.set("Apache 2.0")
		url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
	}
}
