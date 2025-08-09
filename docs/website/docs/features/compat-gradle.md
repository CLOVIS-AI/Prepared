# Gradle plugins

Gradle build logic can often become quite complex. Much like production code, it should be well tested to discover edge cases.

All types of Gradle plugins can be tested (settings plugins, project plugins, precompiled script plugins…).

!!! info "Configuration"
    Add a dependency on `dev.opensavvy.prepared:compat-gradle` to use the features on this page.

    See the [reference](https://prepared.opensavvy.dev/api-docs/compat/compat-gradle/index.html).

Prepared provides helpers to entire Gradle builds inside tests. Typically, this is done to test a plugin. In this example, we will create a plugin and test its behavior.

The project should be similar to:
```text
src/
    main/kotlin/foo/bar/baz/
        MainPlugin.kt  
    test/kotlin/foo/bar/baz/
        MainPluginTest.kt
build.gradle.kts
```
with the following files:
```kotlin title="build.gradle.kts"
plugins {
	`kotlin-dsl` //(1)!
}

gradlePlugin {
	plugins {
		create("my-convention-plugin") {
			id = "main-plugin"
			implementationClass = "foo.bar.baz.MainPlugin"
		}
	}
}

dependencies {
	testImplementation("dev.opensavvy.prepared:runner-kotlin-test:VERSION_HERE") //(2)!
    testImplementation("dev.opensavvy.prepared:compat-gradle:VERSION_HERE")
}
```

1.  The `kotlin-dsl` plugin allows creating Gradle plugins compatible with the current Gradle version. Do **not** apply the `kotlin("jvm")` plugin!
2.  See available versions [here](https://central.sonatype.com/artifact/dev.opensavvy.prepared/compat-gradle). 

```kotlin title="src/main/kotlin/foo/bar/baz/MainPlugin.kt"
import org.gradle.api.*

abstract class MainPlugin : Plugin<Project> {
	override fun apply(target: Project) {
		target.tasks.register("createFileTask") {
			val myFile = File("myfile.txt")
			myFile.createNewFile()
			myFile.writeText("HELLO FROM MY PLUGIN")
		}
	}
}
```

Now, we can write tests for this plugin:
```kotlin title="src/test/kotlin/foo/bar/baz/MainPluginTest.kt"
class MainPluginTest : TestExecutor() {
	override fun SuiteDsl.register() {
		val setup by prepared { //(1)!
			gradle.settingsKts("""
				include("foo")
			""".trimIndent()) //(2)!
            
			gradle.rootProject.buildKts("") //(3)!
            
			gradle.project("foo").buildKts("""
				plugins {
					id("main-plugin")
				}
			""".trimIndent()) //(4)!
		}
        
		val myFile by gradle.project("foo").dir() / "myfile.txt"
        
		test("The task :createFileTask creates a file") {
			setup() //(5)!
            
			gradle.runner()
				.withPluginClasspath() //(6)!
				.withArguments("createFileTask") //(7)!
				.build()
            
			check(myFile().readText() == "HELLO FROM MY PLUGIN")
		}
	}
}
```

1.  Declare the project setup in a [prepared value](prepared-values.md) for convenience: we will be able to reuse it between multiple tests.
2.  Use the [`gradle`](https://prepared.opensavvy.dev/api-docs/compat/compat-gradle/opensavvy.prepared.compat.gradle/gradle.html) extension point and its [`settingsKts`](https://prepared.opensavvy.dev/api-docs/compat/compat-gradle/opensavvy.prepared.compat.gradle/settings-kts.html) function to create the `settings.gradle.kts` file.
3.  Create an empty root `build.gradle.kts` file.
4.  Create a `foo/build.gradle.kts` file that applies our plugin.
5.  Invokes the [prepared value](prepared-values.md) we defined earlier.
6.  Injects the current classpath into the build. Since the plugin we are building is in the current classpath, it will be injected into the test project, which is why we didn't need to specify a version when using `id("my-plugin")`.
7.  The command-line arguments passed when invoking Gradle.

Each test using the `gradle` extension point creates its own [temporary directory](files.md#creating-temporary-files-and-directories) in which all files and the build happen. Its path is always logged to the standard output: if the test fails, you can visit it yourself easily to inspect the state of all files.

If the test is successful, the temporary directory is deleted automatically.

!!! info "Official tutorial"
    Learn more about writing plugins in the [official documentation](https://docs.gradle.org/current/userguide/writing_plugins.html). Prepared is just syntax sugar on top of the official tooling, so all techniques described there should work with Prepared.
