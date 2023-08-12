plugins {
	id("conventions.kotlin")
	application
}

application {
	mainClass.set("opensavvy.playground.app.MainKt")
}

dependencies {
	implementation(projects.core)

	testImplementation(libs.kotlin.test)
}
