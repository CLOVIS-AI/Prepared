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

	val commonMain by sourceSets.getting {
		dependencies {
			api(projects.suite)
			api(libs.kotlinx.datetime)
		}
	}

	val commonTest by sourceSets.getting {
		dependencies {
			implementation(projects.runners.runnerKotest)
		}
	}
}

library {
	name.set("Compatibility with KotlinX.Datetime")
	description.set("Control the passing of time in Prepared tests using objects and methods from KotlinX.Datetime, including Clock and Instant")
	homeUrl.set("https://opensavvy.gitlab.io/groundwork/prepared/api-docs/compat/compat-kotlinx-datetime/index.html")

	license.set {
		name.set("Apache 2.0")
		url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
	}
}
