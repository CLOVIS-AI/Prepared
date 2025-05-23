plugins {
	alias(opensavvyConventions.plugins.base)
	id("dev.opensavvy.dokka-mkdocs")
}

dependencies {
	// List the 'library' projects
	dokka(projects.suite)
	dokka(projects.runners.runnerKotest)
	dokka(projects.runners.runnerKotlinTest)
	dokka(projects.runners.runnerTestInitiative)
	dokka(projects.runners.runnerTestballoon)
	dokka(projects.compat.compatGradle)
	dokka(projects.compat.compatKotlinxDatetime)
	dokka(projects.compat.compatJavaTime)
	dokka(projects.compat.compatFilesystem)
	dokka(projects.compat.compatArrow)
	dokka(projects.compat.compatKtor)
	dokka(projects.compat.compatParameterize)
}
