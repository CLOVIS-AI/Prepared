plugins {
	alias(opensavvyConventions.plugins.base)
	alias(opensavvyConventions.plugins.kotlin.library)
}

kotlin {
	jvm()
}

library {
	name.set("Playground Core")
	description.set("Project template with configured MavenCentral publication")
	homeUrl.set("https://gitlab.com/opensavvy/playgrounds/gradle")

	license.set {
		name.set("Apache 2.0")
		url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
	}
}
