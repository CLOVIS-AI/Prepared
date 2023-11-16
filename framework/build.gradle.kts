plugins {
	alias(opensavvyConventions.plugins.base)
	alias(opensavvyConventions.plugins.kotlin.library)
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
	homeUrl.set("https://opensavvy.gitlab.io/prepared/api-docs/framework/index.html")

	license.set {
		name.set("Apache 2.0")
		url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
	}
}
