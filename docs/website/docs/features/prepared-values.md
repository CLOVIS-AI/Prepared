# Prepared • Isolated fixtures

Prepared values are the flagship feature of OpenSavvy Prepared.

## Understanding test fixtures

### What are test fixtures

"Test fixtures" is the collective name for all initialization logic that is required by a test, but is best defined elsewhere—to enable reuse, because they take a lot of space… Here are a few common examples:

- A test is reading from a file, or comparing the output of some operation to the contents of a file.
- A test must access an external service (a running database, a TestContainer…).
- The test data is large and reused between tests.

See also the [Wikipedia definition](https://en.wikipedia.org/wiki/Test_fixture#Software).

### Implicit fixtures

Traditionally, fixtures are declared implicitly using functions such as `setUp` and `tearDown`:
```kotlin
class FooTest {
	
	lateinit var database: Database
	
	@BeforeTest
	fun createDatabase() {
		database = Database.startLocal()
	}
	
	@AfterTest
	fun closeDatabase() {
		database.close()
	}
	
	@Test
	fun useDatabase() {
		database.listUsers() shouldBe emptyList()
	}
}
```

This approach has a few downsides:

- There is a lot of code for not much behavior.
- All tests in the class always initialize the fixture—but in reality, not all tests may require it.
- It is difficult to know the entire behavior of a test, since fixtures are implicitly used.
- If multiple fixtures depend on each other, it is difficult to understand the execution order.
- Most frameworks do not support coroutines in fixtures…
- …and when they do, fixtures are not executed in the same context as the test.

## Using prepared values

Prepared values are lazy data generators that are executed when they are first referred to by a test, then return the same value for the remainder of the test. Each test receives its own value that is independent of any other test.

Prepared values can be instantiated anywhere, including at the top-level, using the [prepared builder](https://opensavvy.gitlab.io/groundwork/prepared/api-docs/suite/opensavvy.prepared.suite/prepared.html):
```kotlin
val adminEmail by prepared { "my-account-${random.nextInt()}@mail.com" }
```

Any test can refer to them using the function call syntax:
```kotlin
test("First test") {
    adminEmail() shouldEndWith "@mail.com"
	
	// Within a test, all calls are guaranteed to return the exact same value.
    adminEmail() shouldBe adminEmail()
}

test("Second test") {
	// This test will receive its own value
	// that is shared with no other test.
    adminEmail() shouldEndWith "@mail.com"
}
```

??? note "Using randomized identifiers"
    When executing tests against an external stateful service (e.g. a database), if we hardcode identifiers like email addresses, then test suites can no longer be run safely concurrently, as all tests refer to the same value.
    Instead, we prefer using randomly generated identifiers, like the email in this example.

### Creating multiple prepared values from the same generator

The prepared builder can be used as a generator that is bound to multiple values, to generate multiple fixtures from the same initialization logic:
```kotlin
// Notice the 'by' / '=' difference compared to the previous example.
val prepareEmail = prepared { "my-account-${random.nextInt()}@mail.com" }

// The 'by' operator binds the generator to a name.
val adminEmail by prepareEmail
val userEmail by prepareEmail

test("Test") {
	// The same value is returned within a single test.
	adminEmail() shouldBe adminEmail()

	// Different bound values have different values.
	userEmail() shouldNotBe adminEmail()
}
```

??? info "Naming conventions"
    Unbound prepared generators should be prefixed by `prepare` to avoid confusion.

### Chaining prepared values

Prepared values can do anything a test can do, including referring to other prepared values:
```kotlin
val database by prepared { Database.connectLocal() }

val admin by prepared {
	database().createUser("Admin", adminEmail())
}

test("Example") {
	admin().email shouldBe adminEmail()
}
```

When this test executes:

- `admin()` is called, and in turn:
    - `database()` is called,
    - `adminEmail()` is called,
- `adminEmail()` is called again, and returns the already-generated value.

This pattern is the most important feature of prepared values: they allow splitting data generation through multiple layers of fixtures without worrying about seeing different data.

!!! tip
    All accesses to a prepared values print their current state to the standard output, to avoid any confusion about initialization order.

### Prepared values have access to the test context

Because prepared values must be explicitly referred to by a test, we can control their environment. For example, we can set the randomness seed or the virtual time before accessing any prepared value, and they will be impacted by the environment:
```kotlin
test("Edge case") {
	random.setSeed(4597)
	adminEmail() shouldBe adminEmail()
}
```
This allows easily controlling fixtures to reproduce edge cases that happened in CI, etc.
