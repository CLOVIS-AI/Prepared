plugins {
	alias(opensavvyConventions.plugins.base)
	alias(opensavvyConventions.plugins.kotlin.library)
}

kotlin {
	jvm()
	js {
		browser()
		nodejs()
	}

	sourceSets.commonTest.dependencies {
		implementation(opensavvyConventions.aligned.kotlin.test.annotations)
		implementation(opensavvyConventions.aligned.kotlin.test.common)
	}

	sourceSets.jvmTest.dependencies {
		implementation(opensavvyConventions.aligned.kotlin.test.junit5)
	}

	sourceSets.jsTest.dependencies {
		implementation(opensavvyConventions.aligned.kotlin.test.js)
	}
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
