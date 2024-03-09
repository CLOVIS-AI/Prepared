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
		// region OpenSavvy Conventions

		maven {
			name = "opensavvy-gradle-conventions"
			url = uri("https://gitlab.com/api/v4/projects/51233470/packages/maven")

			metadataSources {
				gradleMetadata()
				mavenPom()
			}

			content {
				includeGroupAndSubgroups("dev.opensavvy")
			}
		}

		// endregion
		// region Standard repositories

		gradlePluginPortal()
		google()

		// endregion
	}
}

plugins {
	id("dev.opensavvy.conventions.settings") version "1.1.0"
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
