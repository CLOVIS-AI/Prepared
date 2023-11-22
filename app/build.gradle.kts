plugins {
	alias(opensavvyConventions.plugins.base)
	alias(opensavvyConventions.plugins.kotlin.application)
}

kotlin {
	jvm {
		withJava() // required by the application plugin
	}

	val commonMain by sourceSets.getting {
		dependencies {
			implementation(projects.core)
		}
	}

	val commonTest by sourceSets.getting {
		dependencies {
			implementation(opensavvyConventions.aligned.kotlin.test.junit5)
		}
	}
}

application {
	mainClass.set("opensavvy.playground.app.MainKt")
}
