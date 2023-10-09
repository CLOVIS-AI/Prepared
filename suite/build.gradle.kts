plugins {
	id("conventions.base")
	id("conventions.kotlin")
	id("conventions.library")
}

kotlin {
	jvm()
	js {
		nodejs()
		browser()
	}
	linuxX64()

	val commonMain by sourceSets.getting {
		dependencies {
			api(libs.kotlinx.coroutines.core)
			api(libs.kotlinx.coroutines.test)
		}
	}

	val commonTest by sourceSets.getting {
		dependencies {
			implementation(projects.framework)
		}
	}
}

library {
	name.set("Prepared test suite declarations: suite DSL, time control, lazy fixtures, coroutine support…")
	description.set("Prepared is a magicless test framework for Kotlin Multiplatform")
	homeUrl.set("https://opensavvy.gitlab.io/prepared/documentation/suite/index.html")
}
