plugins {
	alias(opensavvyConventions.plugins.base)
	alias(opensavvyConventions.plugins.kotlin.library)
	alias(libs.plugins.testBalloon)
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
			implementation(projects.runners.runnerTestballoon)
		}
	}
}

library {
	name.set("Filesystem access")
	description.set("Create and check files from the filesystem")
	homeUrl.set("https://prepared.opensavvy.dev/features/files.html")

	license.set {
		name.set("Apache 2.0")
		url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
	}
}

kotlin {
	jvmToolchain(11)
}
