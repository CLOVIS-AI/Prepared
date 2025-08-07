# Files

When testing some file-based tools, it may be necessary to create temporary files or directories, execute a test, and then observe the contents of the files.

Prepared provides helpers to refer to files even before they are created.

!!! info "Configuration"
    Add a dependency on `dev.opensavvy.prepared:compat-filesystem` to use the features on this page. Not all platforms are supported. 

    See the [reference](https://prepared.opensavvy.dev/api-docs/compat/compat-filesystem/index.html).

## Creating temporary files and directories

[New files](https://prepared.opensavvy.dev/api-docs/compat/compat-filesystem/opensavvy.prepared.compat.filesystem/create-random-file.html) and [new directories](https://prepared.opensavvy.dev/api-docs/compat/compat-filesystem/opensavvy.prepared.compat.filesystem/create-random-directory.html) can be created as [prepared values](prepared-values.md):

```kotlin
val workingDirectory by createRandomDirectory()
val html by workingDirectory / "index.html" // (1)!
val css by workingDirectory / "index.css"

val output = workingDirectory / "dist"

test("…") {
	html().writeText("…")
	css().writeText("…")
	
	// Do something…
	
	check(output().readText() == "…")
}
```

1.  The `/` operator can be used to refer to child files or directories before the test starts executing. [Learn more](https://prepared.opensavvy.dev/api-docs/compat/compat-filesystem/opensavvy.prepared.compat.filesystem/div.html).

The files are based on prepared values, so they are created lazily as they are needed, and each test gets a different set of files to work with, ensuring no two tests can attempt to modify the same files.

All the created files are deleted automatically if the test is successful. However, if the test fails, the files are kept on disk in the operating system's temporary directory, and their path is printed in the test output, so you can open them and view their exact state.

## Reading Java resources

On the JVM, it is possible to organize resources files in the same packages as source classes to avoid conflicts. To access these files, use the [resource](https://prepared.opensavvy.dev/api-docs/compat/compat-filesystem/opensavvy.prepared.compat.filesystem.resources/resource.html) helper:

```kotlin
package foo.bar.baz

private object TargetClass

val test by resource<TargetClass>("test.txt")
	.read()

test("Value should be the same as the file") {
	check(someSystem().toString() == test())
}
```

This will read the file `test.txt` that is placed in the same package as the provided class (`TargetClass`), as a [prepared value](prepared-values.md).
In this example, the file should be place in `src/test/resources/foo/bar/baz/test.txt` to match the package declaration.

!!! tip
    If you are using a class-based test runner, you can use the test class itself as parameter. For example, with the Kotest runner:
    ```kotlin
    class OutputTest : PreparedSpec({
    	val dump = resource<OutputTest>("dump.txt")
            .read()

        test("Load a database from a dump") {
            Database.loadFrom(dump())
        }
    })
    ```
