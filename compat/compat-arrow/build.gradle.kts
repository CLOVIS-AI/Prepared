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
		browser()
	}
	linuxX64()

	val commonMain by sourceSets.getting {
		dependencies {
			api(projects.suite)
			api(libs.arrow.core)
		}
	}

	val commonTest by sourceSets.getting {
		dependencies {
			implementation(projects.framework)
			implementation(projects.runners.runnerKotest)
		}
	}
}

library {
	name.set("Compatibility with Arrow")
	description.set("Bind Either and Validated to fail test cases, with origin tracing")
	homeUrl.set("https://opensavvy.gitlab.io/prepared/documentation/compat-arrow/index.html")
}
