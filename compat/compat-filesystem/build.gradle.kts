plugins {
	alias(opensavvyConventions.plugins.base)
	alias(opensavvyConventions.plugins.kotlin.library)
}

kotlin {
	jvm()

	sourceSets.commonMain {
		dependencies {
			api(projects.suite)
		}
	}

	sourceSets.commonTest {
		dependencies {
			implementation(projects.runners.runnerKotest)
		}
	}
}

library {
	name.set("Filesystem access")
	description.set("Create and check files from the filesystem")
	homeUrl.set("https://opensavvy.gitlab.io/groundwork/prepared/api-docs/compat/compat-filesystem/index.html")

	license.set {
		name.set("Apache 2.0")
		url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
	}
}
