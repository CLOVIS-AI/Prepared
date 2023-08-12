plugins {
	id("conventions.kotlin")
	application
}

application {
	mainClass.set("opensavvy.playground.MainKt")
}

dependencies {
	testImplementation(libs.kotlin.test)
}
