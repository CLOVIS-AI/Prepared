/*
 * Copyright (c) 2025, OpenSavvy and contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
	alias(opensavvyConventions.plugins.base)
	alias(opensavvyConventions.plugins.kotlin.library)
	alias(libs.plugins.testBalloon)
}

@OptIn(ExperimentalWasmDsl::class)
kotlin {
	jvm()
	js {
		browser()
		nodejs()
	}
	linuxX64()
	linuxArm64()
	macosX64()
	macosArm64()
	iosArm64()
	iosX64()
	iosSimulatorArm64()
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
	wasmWasi {
		nodejs()
	}

	sourceSets.commonMain {
		dependencies {
			api(libs.kotlinx.coroutines.core)
			api(libs.kotlinx.coroutines.test)
		}
	}

	sourceSets.jvmMain {
		dependencies {
			implementation(libs.kotlinx.coroutines.debug)
		}
	}

	sourceSets.commonTest {
		dependencies {
			implementation(projects.runners.runnerTestballoon)
		}
	}
}

library {
	name.set("Suite")
	description.set("Magicless test framework for Kotlin Multiplatform")
	homeUrl.set("https://prepared.opensavvy.dev")

	license.set {
		name.set("Apache 2.0")
		url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
	}
}

kotlin {
	jvmToolchain(11)
}
