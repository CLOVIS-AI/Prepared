import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
	alias(opensavvyConventions.plugins.base)
	alias(opensavvyConventions.plugins.kotlin.library)
}

@OptIn(ExperimentalWasmDsl::class)
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
	linuxArm64()
	macosX64()
	macosArm64()
	iosArm64()
	iosSimulatorArm64()
	iosX64()
	watchosX64()
	watchosArm32()
	watchosArm64()
	watchosSimulatorArm64()
	tvosX64()
	tvosArm64()
	tvosSimulatorArm64()
	mingwX64()
	wasmJs {
		browser()
		nodejs()
	}

	sourceSets.commonMain {
		dependencies {
			api(projects.suite)

			api("io.kotest:kotest-framework-engine:${libsCommon.versions.kotest.asProvider().get()}")
		}
	}

	sourceSets.jvmMain {
		dependencies {
			api("io.kotest:kotest-runner-junit5:${libsCommon.versions.kotest.asProvider().get()}")
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
	homeUrl.set("https://opensavvy.gitlab.io/groundwork/prepared/api-docs/runners/runner-kotest/index.html")

	license.set {
		name.set("Apache 2.0")
		url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
	}
}

kotlin {
	jvmToolchain(11)
}
