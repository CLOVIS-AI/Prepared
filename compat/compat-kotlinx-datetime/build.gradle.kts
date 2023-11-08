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
			api(projects.suite)
			api(libs.kotlinx.datetime)
		}
	}

	val commonTest by sourceSets.getting {
		dependencies {
			implementation(projects.framework)
		}
	}
}

library {
	name.set("Compatibility with KotlinX.Datetime")
	description.set("Control the passing of time in Prepared tests using objects and methods from KotlinX.Datetime, including Clock and Instant")
	homeUrl.set("https://opensavvy.gitlab.io/prepared/api-docs/compat/compat-kotlinx-datetime/index.html")
}
