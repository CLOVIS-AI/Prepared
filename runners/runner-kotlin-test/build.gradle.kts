plugins {
	id("conventions.base")
	id("conventions.kotlin")
	id("conventions.library")
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

			implementation(libs.kotlin.test.common)
			implementation(libs.kotlin.test.annotations)
		}
	}

	val commonTest by sourceSets.getting {
		dependencies {
			implementation(projects.compat.compatKotlinxDatetime)
		}
	}

	val jvmMain by sourceSets.getting {
		dependencies {
			implementation(libs.kotlin.test.junit5)
		}
	}

	val jsMain by sourceSets.getting {
		dependencies {
			implementation(libs.kotlin.test.js)
		}
	}
}

tasks.withType(Test::class) {
	testLogging {
		events("skipped", "failed", "passed")
	}
}
