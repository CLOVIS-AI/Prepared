plugins {
	alias(opensavvyConventions.plugins.base)
	alias(opensavvyConventions.plugins.kotlin.application)
}

kotlin {
	jvm {
		withJava() // required by the application plugin
	}

	sourceSets.commonMain.dependencies {
		implementation(projects.core)
	}
}

application {
	mainClass.set("opensavvy.playground.app.MainKt")
}
