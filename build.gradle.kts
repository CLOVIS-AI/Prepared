/*
 * This is the root project.
 *
 * The root project should remain empty. Code, etc, should happen in subprojects, and the root project should only delegate
 * work to subprojects.
 *
 * In particular, *avoid using the 'allprojects' and 'subprojects' Gradle blocks!* They slow down the configuration phase.
 */

plugins {
	id("conventions.base")
	id("conventions.root")

	// Some plugins *must* be configured on the root project.
	// In these cases, we explicitly tell Gradle not to apply them.
	alias(libs.plugins.kotlin) apply false

	alias(libs.plugins.dokkatoo)
}

dependencies {
	// List the 'library' projects
	dokkatoo(projects.core)

	// This is required at the moment, see https://github.com/adamko-dev/dokkatoo/issues/14
	dokkatooPluginHtml(
		dokkatoo.versions.jetbrainsDokka.map { dokkaVersion ->
			"org.jetbrains.dokka:all-modules-page-plugin:$dokkaVersion"
		}
	)
}
