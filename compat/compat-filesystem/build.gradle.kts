plugins {
	alias(opensavvyConventions.plugins.base)
	alias(opensavvyConventions.plugins.kotlin.library)
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
	homeUrl.set("https://opensavvy.gitlab.io/prepared/api-docs/compat/compat-filesystem/index.html")

	license.set {
		name.set("Apache 2.0")
		url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
	}
}
