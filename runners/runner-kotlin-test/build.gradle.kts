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

	sourceSets.commonMain {
		dependencies {
			api(projects.suite)

			api(libsCommon.kotlin.test)
		}
	}

	sourceSets.commonTest {
		dependencies {
			implementation(projects.compat.compatKotlinxDatetime)
		}
	}

	sourceSets.jvmMain {
		dependencies {
			api(libsCommon.kotlin.test.junit5)
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
	homeUrl.set("https://opensavvy.gitlab.io/groundwork/prepared/api-docs/runners/runner-kotlin-test/index.html")

	license.set {
		name.set("Apache 2.0")
		url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
	}
}

kotlin {
	jvmToolchain(11)
}
