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

	sourceSets.commonMain {
		dependencies {
			api(projects.suite)
			api(libs.ktor.testHost)
		}
	}

	sourceSets.commonTest {
		dependencies {
			implementation(projects.runners.runnerKotest)
		}
	}
}

library {
	name.set("Compatibility with Ktor")
	description.set("Declare Ktor in-memory servers and clients as test fixtures")
	homeUrl.set("https://opensavvy.gitlab.io/groundwork/prepared/api-docs/compat/compat-arrow/index.html")

	license.set {
		name.set("Apache 2.0")
		url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
	}
}
