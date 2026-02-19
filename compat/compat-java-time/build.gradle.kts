/*
 * Copyright (c) 2026, OpenSavvy and contributors.
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

plugins {
	alias(opensavvyConventions.plugins.base)
	alias(opensavvyConventions.plugins.kotlin.library)
	alias(libs.plugins.testBalloon)
}

kotlin {
	jvm()

	sourceSets.jvmMain {
		dependencies {
			api(projects.suite)
		}
	}

	sourceSets.jvmTest {
		dependencies {
			implementation(projects.runners.runnerTestballoon)
		}
	}
}

library {
	name.set("Compatibility with java.time")
	description.set("Control the passing of time in Prepared tests using objects and methods from java.time, including Clock and Instant")
	homeUrl.set("https://prepared.opensavvy.dev/features/time.html")

	license.set {
		name.set("Apache 2.0")
		url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
	}
}

tapmoc {
	java(11)
}
