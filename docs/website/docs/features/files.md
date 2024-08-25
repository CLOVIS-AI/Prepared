# Temporary files

When testing some file-based tools, it may be necessary to create temporary files or directories, execute a test, and then observe the contents of the files.

Prepared provides helpers to refer to files even before they are created.

!!! info "Configuration"
    Add a dependency on `dev.opensavvy.prepared:compat-filesystem` to use the features on this page. Not all platforms are supported. 

    See the [reference](https://opensavvy.gitlab.io/groundwork/prepared/api-docs/compat/compat-filesystem/index.html).

## Creating temporary files and directories

[New files](https://opensavvy.gitlab.io/groundwork/prepared/api-docs/compat/compat-filesystem/opensavvy.prepared.compat.filesystem/create-random-file.html) and [new directories](https://opensavvy.gitlab.io/groundwork/prepared/api-docs/compat/compat-filesystem/opensavvy.prepared.compat.filesystem/create-random-directory.html) can be created as [prepared values](prepared-values.md):

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

1.  The `/` operator can be used to refer to child files or directories before the test starts executing. [Learn more](https://opensavvy.gitlab.io/groundwork/prepared/api-docs/compat/compat-filesystem/opensavvy.prepared.compat.filesystem/div.html).

The files are based on prepared values, so they are created lazily as they are needed, and each test gets a different set of files to work with, ensuring no two tests can attempt to modify the same files.

All the created files are deleted automatically if the test is successful. However, if the test fails, the files are kept on disk in the operating system's temporary directory, and their path is printed in the test output, so you can open them and view their exact state.
