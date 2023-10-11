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
		}
	}
}

library {
	name.set("Prepared Framework")
	description.set("Collection of libraries to make Prepared as useful as possible, with as little setup as possible.")
	homeUrl.set("https://opensavvy.gitlab.io/prepared/documentation/framework/index.html")
}
