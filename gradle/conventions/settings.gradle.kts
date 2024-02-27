rootProject.name = "conventions"

dependencyResolutionManagement {
	versionCatalogs {
		create("libs") {
			from(files("../libs.versions.toml"))
		}
	}
}

pluginManagement {
	repositories {
		gradlePluginPortal()

		maven {
			name = "opensavvy-gradle-conventions"
			url = uri("https://gitlab.com/api/v4/projects/51233470/packages/maven")
		}
	}
}

plugins {
	id("dev.opensavvy.conventions.settings") version "1.1.0"
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
