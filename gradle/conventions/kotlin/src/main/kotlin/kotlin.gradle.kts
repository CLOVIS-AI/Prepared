package conventions

plugins {
	// Currently, it is not possible to use version catalogs here…
	kotlin("jvm")
	id("conventions.versioning")
}

repositories {
	mavenCentral()
	google()
}

kotlin {
	jvmToolchain(17)
}
