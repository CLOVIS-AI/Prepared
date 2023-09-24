package conventions

plugins {
	id("io.github.gradle-nexus.publish-plugin")
}

nexusPublishing {
	repositories {
		sonatype {
			nexusUrl.set(uri("https://s01.oss.sonatype.org/service/local/"))
			snapshotRepositoryUrl.set(uri("https://s01.oss.sonatype.org/content/repositories/snapshots/"))

			username.set(System.getenv("OSSRH_USERNAME"))
			password.set(System.getenv("OSSRH_PASSWORD"))
		}
	}
}
