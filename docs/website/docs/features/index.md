# Features overview

Prepared adds many helpers to facilitate writing tests. To learn about them, either select them in the menu on the left of this page, or click on one of the annotations in this complete example:

??? warning "This example is not idiomatic!"
    This example is written specifically to showcase all features in a single place. It is not considered idiomatic.
    For more information, visit [the best practices section](../practices/index).

??? note "Assertion library"
    This example uses the Kotest assertion library.
    [Learn more about assertion libraries](../tutorials/index#assertion-libraries).

```kotlin
fun SuiteDsl.testUsers( // (1)!
    users: Prepared<UserService>, // (4)!
) {
    suite("Example") { // (2)!
        test("Hello world") { // (3)!
            "Hello world" shouldBe "Hello world"
        }

        test("Another test") {
            (2 + 2) shouldBe 4
        }
    }

    suite("Test fixtures") {
        val adminEmail by prepared { // (5)!
            "account-${random.nextInt()}@mail.com" // (9)!
        }

        val admin by prepared {
            val user = users().createUser(adminEmail())
            
            cleanUp("Deleting the created admin") { // (7)! 
                users().deleteUser(it) 
            }
            
            launchInBackground { // (8)!
                while (true) {
                    delay(1000)
                    users().increaseAgeOf(it)
                }
            }
            
            user
        }

        val bio by shared { // (6)!
            Path.of("foo", "bar").readText()
        }

        test("The user was created with the correct email") {
            admin().email shouldBe adminEmail()
        }

        test("The user was created with the correct bio") {
            admin().bio shouldBe bio()
        }
    }
}
```

1. The `SuiteDsl` receiver enables the declaration of tests and suites that are compatible with any test runner.
   To learn more, see [the getting started section](../tutorials/index#test-runners).
2. The `suite` function creates a nested container to organize related tests into a single unit.
   [Learn more](https://opensavvy.gitlab.io/groundwork/prepared/api-docs/suite/opensavvy.prepared.suite/-suite-dsl/suite.html).
3. The `test` function declares the existence of a test.
   [Learn more](https://opensavvy.gitlab.io/groundwork/prepared/api-docs/suite/opensavvy.prepared.suite/-suite-dsl/test.html).
4. Prepared values are fixture generators that always return the same value in a context of a single test, but return different values for different tests.
   [Learn more](prepared-values.md).
5. Prepared values are fixture generators that always return the same value in a context of a single test, but return different values for different tests.
   [Learn more](prepared-values.md).
6. Shared values are lazy generators whose output is shared between multiple tests.
   [Learn more](shared-values.md).
7. Finalizers are useful to co-locate clean up code next to value generation.
   [Learn more](finalizers.md).
8. Tests can create asynchronous tasks that run in the foreground or in the background.
   [Learn more](async.md).
9. Generate randomized values with full seed control using the `random` helper.
   [Learn more](random.md).
