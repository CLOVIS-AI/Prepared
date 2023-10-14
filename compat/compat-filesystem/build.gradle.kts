plugins {
	id("conventions.base")
	id("conventions.kotlin")
	id("conventions.library")
}

kotlin {
	jvm()

	val commonMain by sourceSets.getting {
		dependencies {
			api(projects.suite)
		}
	}

	val commonTest by sourceSets.getting {
		dependencies {
			implementation(projects.framework)
		}
	}
}

library {
	name.set("Filesystem access")
	description.set("Create and check files from the filesystem")
	homeUrl.set("https://opensavvy.gitlab.io/prepared/documentation/compat-filesystem/index.html")
}
