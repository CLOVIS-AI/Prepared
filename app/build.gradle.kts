import java.awt.Color.red

plugins {
	id("conventions.base")
	id("conventions.kotlin")
	application
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
			implementation(playgroundLibs.kotlin.test)
		}
	}
}

application {
	mainClass.set("opensavvy.playground.app.MainKt")
}
