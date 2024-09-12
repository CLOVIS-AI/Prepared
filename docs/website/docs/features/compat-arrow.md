# Arrow typed errors

[Arrow typed errors](https://arrow-kt.io/learn/typed-errors/working-with-typed-errors/) are a powerful way to declare the possible failure cases of a function. Because failure situations are encoded directly in the function's type (unlike exceptions), we can use type parameters to create abstractions over certain failure cases to better handle them.

!!! info "Configuration"
    Add a dependency on `dev.opensavvy.prepared:compat-arrow` to use the features on this page.

    See the [reference](https://opensavvy.gitlab.io/groundwork/prepared/api-docs/compat/compat-arrow/index.html).

!!! info
    The examples on this page use the [Kotest assertion library](../tutorials/index.md#assertion-libraries).

## Testing success or failure

We want to test the following function:
```kotlin
data object NegativeSquareRoot

context(Raise<NegativeSquareRoot>) //(1)!
fun sqrt(value: Double): Double {
	ensure(value >= 0) { NegativeSquareRoot } //(2)!
	return kotlin.math.sqrt(value)
}
```

1.  Failure conditions are declared as part of the function's signature. In this example, we use the experimental [context parameter](https://github.com/Kotlin/KEEP/blob/master/proposals/context-receivers.md) syntax. <br/>If you do not have access to this syntax, you can also use regular extension receivers: `#!kotlin fun Raise<NegativeSquareRoot>.sqrt(value: Double): Double { … }`.
2.  [`ensure`](https://apidocs.arrow-kt.io/arrow-core/arrow.core.raise/ensure.html) is Arrow's equivalent to [`require`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/require.html) and [`check`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/check.html): we test a condition, and raise a failure if it is `false`.

To test a successful case, we use the [`failOnRaise`](https://opensavvy.gitlab.io/groundwork/prepared/api-docs/compat/compat-arrow/opensavvy.prepared.compat.arrow.core/fail-on-raise.html) function:
```kotlin
test("√4 is successful") {
	failOnRaise {
		sqrt(4.0)
	} shouldBe 2.0
}
```

To test a failed case, we use the [`assertRaises`](https://opensavvy.gitlab.io/groundwork/prepared/api-docs/compat/compat-arrow/opensavvy.prepared.compat.arrow.core/assert-raises.html) or [`assertRaisesWith`](https://opensavvy.gitlab.io/groundwork/prepared/api-docs/compat/compat-arrow/opensavvy.prepared.compat.arrow.core/assert-raises-with.html) functions:
```kotlin
test("√-1 raises") {
	assertRaises(NegativeSquareRoot) { //(1)!
		sqrt(-1.0)
	}
}

test("√-1 raises") {
	assertRaisesWith<NegativeSquareRoot> { //(2)!
		sqrt(-1.0)
	}
}
```

1.  Asserts that a function raises a specific value.
2.  Asserts that a function raises a specific type.

## Error tracing

Prepared takes advantage of the `Raise` DSL's tracing capabilities: when an unexpected failure happens, a proper stack trace is generated.

For example, the following code:
```kotlin
fun Raise<Int>.a(): Unit = raise(42)
fun Raise<Int>.b() = a()
fun Raise<Int>.c() = b()
fun Raise<Int>.d() = c()
fun Raise<Int>.e() = d()

test("Test tracing") {
    failOnRaise {
        e()
    }
}
```
will fail with an exception that contains all intermediary functions:
```text
An operation raised 42.
	at arrow.core.raise.DefaultRaise.raise(Fold.kt:239)
	at foo.FailOnRaiseTestKt.a(FailOnRaiseTest.kt:28)    ←
	at foo.FailOnRaiseTestKt.b(FailOnRaiseTest.kt:29)    ←
	at foo.FailOnRaiseTestKt.c(FailOnRaiseTest.kt:30)    ←
	at foo.FailOnRaiseTestKt.d(FailOnRaiseTest.kt:31)    ←
	at foo.FailOnRaiseTestKt.e(FailOnRaiseTest.kt:32)    ←
	at foo.FailOnRaiseTest$1$3.invokeSuspend(FailOnRaiseTest.kt:23)
	at foo.FailOnRaiseTest$1$3.invoke(FailOnRaiseTest.kt)
	at foo.FailOnRaiseTest$1$3.invoke(FailOnRaiseTest.kt)
	at opensavvy.prepared.suite.RunTestKt$runTestDslSuspend$2.invokeSuspend(RunTest.kt:42)
```

If, instead, we use a naive testing approach, like most test frameworks do:
```kotlin
fun Raise<Int>.a(): Unit = raise(42)
fun Raise<Int>.b() = a()
fun Raise<Int>.c() = b()
fun Raise<Int>.d() = c()
fun Raise<Int>.e() = d()

test("Test without tracing") {
	val result = either {
		e()
	}
	assertEquals(Unit.right(), result)
}
```
we get the following, much less useful, error:
```text
AssertionFailedError: expected:<Either.Right(kotlin.Unit)> but was:<Either.Left(42)>
	at foo.FailOnRaiseTest$1$3.invokeSuspend(FailOnRaiseTest.kt:28)
	at foo.FailOnRaiseTest$1$3.invoke(FailOnRaiseTest.kt)
	at foo.FailOnRaiseTest$1$3.invoke(FailOnRaiseTest.kt)
	at opensavvy.prepared.suite.RunTestKt$runTestDslSuspend$2.invokeSuspend(RunTest.kt:42)
```
