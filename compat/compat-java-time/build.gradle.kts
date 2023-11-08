plugins {
	id("conventions.base")
	id("conventions.kotlin")
	id("conventions.library")
}

kotlin {
	jvm()

	val jvmMain by sourceSets.getting {
		dependencies {
			api(projects.suite)
		}
	}

	val jvmTest by sourceSets.getting {
		dependencies {
			implementation(projects.framework)
		}
	}
}

library {
	name.set("Compatibility with java.time")
	description.set("Control the passing of time in Prepared tests using objects and methods from java.time, including Clock and Instant")
	homeUrl.set("https://opensavvy.gitlab.io/prepared/documentation/compat-java-time/index.html")
}
