plugins {
	id("conventions.base")
	id("conventions.kotlin")
	id("conventions.library")
}

kotlin {
	jvm()
}

library {
	name.set("Playground Core")
	description.set("Project template with configured MavenCentral publication")
	homeUrl.set("https://gitlab.com/opensavvy/playgrounds/gradle")
}
