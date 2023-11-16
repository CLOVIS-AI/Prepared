/*
 * This is the root project.
 *
 * The root project should remain empty. Code, etc, should happen in subprojects, and the root project should only delegate
 * work to subprojects.
 *
 * In particular, *avoid using the 'allprojects' and 'subprojects' Gradle blocks!* They slow down the configuration phase.
 */

plugins {
	alias(opensavvyConventions.plugins.base)
	alias(opensavvyConventions.plugins.root)

	// Some plugins *must* be configured on the root project.
	// In these cases, we explicitly tell Gradle not to apply them.
	alias(opensavvyConventions.plugins.aligned.kotlin) apply false
}

dependencies {
	// List the 'library' projects
	dokkatoo(projects.suite)
	dokkatoo(projects.runners.runnerKotest)
	dokkatoo(projects.runners.runnerKotlinTest)
	dokkatoo(projects.framework)
	dokkatoo(projects.compat.compatGradle)
	dokkatoo(projects.compat.compatKotlinxDatetime)
	dokkatoo(projects.compat.compatJavaTime)
	dokkatoo(projects.compat.compatFilesystem)
	dokkatoo(projects.compat.compatArrow)
}

// region Check the users of the project didn't forget to rename the group

val projectPath: String? = System.getenv("CI_PROJECT_PATH")
if (projectPath != null && projectPath != "opensavvy/playgrounds/gradle" && group == "dev.opensavvy.playground") {
	error("The project is declared to be in the group '$group', which is recognized as the Gradle Playground, but it's hosted in '$projectPath', which is not the Playground. Maybe you forgot to rename the group when importing the Playground in your own project?")
}

// endregion
