# Finalizers

[Finalizers](https://opensavvy.gitlab.io/groundwork/prepared/api-docs/suite/opensavvy.prepared.suite/clean-up.html) allow to execute code at the end of the test.

## Usage

```kotlin
test("Create a user") {
	val database = Database.connectLocal()
	
	cleanUp("Close the database") {
		database.close()
	}
	
	// The rest of the test…
}
```

When the test is over (whether successful or not), the `database.close()` function is called.

!!! note
    If multiple finalizers are registered, they are always executed in reverse order (the last registered is executed first).

### Controlling execution based on test outcome

Finalizers can also be configured to only execute depending on a specific test output:

```kotlin
import java.nio.file.Files
import kotlin.io.path.deleteExisting

test("Compile") {
	val outputFile = Files.createTempFile(null, null)

	cleanUp(onFailure = false) {
		outputFile.deleteExisting()
	}

	// The rest of the test…
}
```

The finalizer will only run if the test is successful: if the test fails, the file will be left on the file system, so we can open it and learn what went wrong.

!!! tip
    The fixture used in this example is already provided by Prepared, see `createRandomFile` and `createRandomDirectory`.

Finalizers are most often used inside [prepared values](prepared-values.md) to co-locate fixture creation and destruction.
