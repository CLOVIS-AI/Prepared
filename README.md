# OpenSavvy Prepared

> This project is experimental.

Testing frameworks are composed of three different components:
- The **assertions** check that values are what we expect,
- The **structure** is the way tests are declared and discovered,
- The **runner** is the program that executes the tests.

OpenSavvy Prepared is a **structure** library: it concentrates on the way tests are declared.
It is possible to use other libraries (e.g. [Kotlin Test](https://kotlinlang.org/api/latest/kotlin.test/), [Kotest](https://kotest.io/), [Strikt](https://strikt.io/)…) to declare assertions.

```kotlin
// Declare tests using a regular Kotlin DSL, no annotations or other magic
fun SuiteDsl.showcase() = suite("Showcase Prepared") {
	test("A simple test") {
		// Use Kotest or any other assertion library
		"Hello world" shouldContain "world"
	}

	// Instantiate test data with coroutine-aware builders
	val database by prepared { Database.connect() }
	val minVersion by prepared { Database.minimalVersion.connect() }
	val testDir by createRandomDirectory()

	// Declare nested test suites
	suite("Dump the database") {
		// Declare tests programmatically
		for (db in listOf(database, minVersion)) {
			test("Dump the database ${db.name}") {
				// Each test gets its own instance of all prepared values
				// (here, each test gets its own output directory)
				val outputDir = testDir()

				db().dumpTo(outputDir)
			}
		}
	}

	test("Control the time and randomness") {
		time.set("2023-10-21T21:08:29Z")
		random.setSeed(123)

		val random = testDir / "random.txt"
		val date = testDir / "now.txt"

		// Only values accessed in the test are prepared;
		// here, the 'database' and 'minVersion' values are not created
		random().writeText(random.nextInt())
		date().writeText(time.now().toString())
	}
}
```

To learn more, [read the documentation](https://opensavvy.gitlab.io/prepared/api-docs/suite/index.html).

## Project structure

- `suite`: Utilities to declare tests and describe how they should run
- `framework`: Default configuration of all modules for ease of use
- `runners`: Compatibility layer for test runners
- `compat`: Compatibility layer for other libraries

## License

This project is licensed under the [Apache 2.0 license](LICENSE).

## Contributing

See [CONTRIBUTING.md](CONTRIBUTING.md).
- To learn more about our coding conventions and workflow, see the [OpenSavvy Wiki](https://gitlab.com/opensavvy/wiki/-/blob/main/README.md#wiki).
- This project is based on the [OpenSavvy Playground](docs/playground/README.md), a collection of preconfigured project templates.
