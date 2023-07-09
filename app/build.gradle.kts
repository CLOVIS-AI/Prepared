plugins {
	alias(libs.plugins.convention.kotlin)
	application
}

application {
	mainClass.set("opensavvy.playground.MainKt")
}

dependencies {
	testImplementation(libs.kotlin.test)
}
