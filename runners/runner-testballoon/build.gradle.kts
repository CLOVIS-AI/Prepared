@file:OptIn(ExperimentalWasmDsl::class)

import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
	alias(opensavvyConventions.plugins.base)
	alias(opensavvyConventions.plugins.kotlin.library)
	alias(libs.plugins.testBalloon)
}

kotlin {
	jvm()
	js {
		browser()
		nodejs()
	}
	linuxX64()
	wasmJs {
		browser()
		nodejs()
	}

	sourceSets.commonMain {
		dependencies {
			api(projects.suite)

			api(libs.testBalloon)
		}
	}
}

library {
	name.set("Execute with TestBalloon")
	description.set("Execute Prepared test suites with the TestBalloon multiplatform-first framework")
	homeUrl.set("https://opensavvy.gitlab.io/groundwork/prepared/api-docs/runners/runner-testballoon/index.html")

	license.set {
		name.set("Apache 2.0")
		url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
	}
}
