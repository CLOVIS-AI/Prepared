# Module Execute with Kotlin-test

Execute Prepared tests alongside tests written using the standard test library.

<a href="https://search.maven.org/search?q=dev.opensavvy.prepared.runner-kotlin-test"><img src="https://img.shields.io/maven-central/v/dev.opensavvy.prepared/runner-kotlin-test.svg?label=Maven%20Central"></a>

## How it works

Because JavaScript doesn't have a concept of visibility, we are able to directly access the internals of kotlin-test
and declare tests ourselves.
To learn more, see [this prototype](https://youtrack.jetbrains.com/issue/KT-46899/Dynamic-test-API#focus=Comments-27-6859886.0-0).

## Limitations

Since kotlin-test does not support nested test suites (as tests normally can only be declared in classes),
the JS implementation doesn't allow nested test suites either.
All suites declared using the Prepared syntax are thus un-nested and executed at the top-level.

Because this accesses the internals of the library directly, there is no guarantee that this keeps working in the future!
Please vote for [KT-46899](https://youtrack.jetbrains.com/issue/KT-46899/Dynamic-test-API#focus=Comments-27-6859886.0-0).
