import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalWasmDsl

plugins {
	alias(opensavvyConventions.plugins.base)
	alias(opensavvyConventions.plugins.kotlin.library)
	alias(opensavvyConventions.plugins.aligned.kotest)
}

@OptIn(ExperimentalWasmDsl::class)
kotlin {
	jvm()
	js {
		nodejs()
		browser()
	}
	linuxX64()
	iosArm64()
	iosSimulatorArm64()
	iosX64()
	wasmJs {
		browser()
		nodejs()
	}

	val commonMain by sourceSets.getting {
		dependencies {
			api(libs.kotlinx.coroutines.core)
			api(libs.kotlinx.coroutines.test)
		}
	}

	val commonTest by sourceSets.getting {
		dependencies {
			implementation(projects.runners.runnerKotest)
		}
	}
}

library {
	name.set("Suite")
	description.set("Magicless test framework for Kotlin Multiplatform")
	homeUrl.set("https://opensavvy.gitlab.io/groundwork/prepared/api-docs/suite/index.html")

	license.set {
		name.set("Apache 2.0")
		url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
	}
}
