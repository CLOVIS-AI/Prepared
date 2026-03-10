# Use Arrow resources in your Kotlin tests

[Arrow resources](https://arrow-kt.io/learn/coroutines/resource-safety/) are a powerful way to encapsulate the declaration of entities along with the logic required to safely close them.

Resources can be viewed as an extension of the `AutoCloseable` interface, into a complete composable DSL.

!!! info "Configuration"
Add a dependency on `dev.opensavvy.prepared:compat-arrow` to use the features on this page.

    See the [reference](https://prepared.opensavvy.dev/api-docs/compat/compat-arrow/index.html).

!!! info
The examples on this page use the [Power Assert assertion library](../tutorials/index.md#assertion-libraries).

Arrow resources are conceptually similar to [prepared values](prepared-values.md):

- Both types represent a value computed in a different step than its declaration. Resources are declared then bound within a `ResourceScope`; prepared values are declared then invoked within a test.
- Both types include information on how to release them. Resources have a `release` action; prepared values can register a [finalizer](finalizers.md).

The Prepared library provides multiple ways to bind Arrow resources to tests. In each case, the resource is initialized once for each test (different tests get different instances), and the release action runs at the end of the test.

!!! note
Note that although Arrow resources' `release` actions gets an `ExitCase`, Prepared currently only distinguishes between success and failure, and doesn't provide the exact exceptions when a failure happens. If this would be useful to you, [please tell us](https://gitlab.com/opensavvy/groundwork/prepared/-/issues/92).

## Using a resource as a prepared value

We will use the example from the [Resource documentation](https://apidocs.arrow-kt.io/arrow-fx-coroutines/arrow.fx.coroutines/-resource/index.html):

```kotlin
val userProcessor: Resource<UserProcessor> = resource({
	UserProcessor().also { it.start() }
}) { p, _ -> p.shutdown() }

val dataSource: Resource<DataSource> = resource({
	DataSource().also { it.connect() }
}) { ds, exitCase ->
	println("Releasing $ds with exit: $exitCase")
	withContext(Dispatchers.IO) { ds.close() }
}

val service: Resource<Service> = resource {
	Service(dataSource.bind(), userProcessor.bind())
}
```

If we have the code above in our production module, there are multiple ways to use it in a test.
The first and simplest way is to use the `asPrepared` conversion function:

```kotlin
val prepareService by service.asPrepared()

test("create() does not return null") {
	checkNotNull(prepareService().create("…"))
}
```

If you only had `userProcessor` and `dataSource` in your production code, you could directly create the `service` as a prepared value:

```kotlin
val service by preparedResource {
	Service(dataSource.bind(), userProcessor.bind())
}
```

The `preparedResource` behaves identically to the [built-in `prepared` function](prepared-values.md), but also provides the capabilities of Arrow's `ResourceScope`.

## Using a resource in a test

Wrapping a resource in a [prepared value](prepared-values.md) is a good pattern, because resources and prepared values are conceptually similar, so we can use them in tests similarly to how we use them in production.

If you want to use a resource in a specific test, you can directly install it:

```kotlin
test("create() does not return null") {
	val s = install(service)

	checkNotNull(s.create("…"))
}
```

You can also use `install` to declare the acquire and release actions separately:

```kotlin
test("create() does not return null") {
	val db = install({
		DataSource().also { it.connect() }
	}) { ds.close() }

	// …
}
```

## Injecting the test's lifecycle into the production code

If your production system needs to know the lifecycle of its caller, you can use `resourceScope()` within a test or within a [prepared value](prepared-values.md):

```kotlin
test("Launch the system") {
	YourApp(resourceScope()).launch()
}
```
