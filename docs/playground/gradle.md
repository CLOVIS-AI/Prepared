# OpenSavvy Playground: Gradle

> [Repository](https://gitlab.com/opensavvy/playgrounds/gradle)

The Gradle playground contains a Hello World idiomatic Gradle project with maintainability and performance in mind.

[TOC]

## The Gradle Wrapper

The Gradle Wrapper (`gradlew`) is a small binary that downloads a specific version of Gradle.
It is configured in the [gradle](../../gradle/wrapper) directory, and started by the [gradlew](../../gradlew) and [gradlew.bat](../../gradlew.bat) scripts.

Using the Wrapper ensures all team members and CI use the exact same version of Gradle.
Because Gradle is responsible for downloading all other project dependencies, the only dependency for using the project is having a half-modern JVM.

To learn more about the Gradle Wrapper, see [its documentation](https://docs.gradle.org/current/userguide/gradle_wrapper.html).
