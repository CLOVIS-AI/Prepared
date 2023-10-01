# OpenSavvy Prepared

> This project is experimental.

Testing libraries are composed of three different components:
- The **assertions** check that values are what we expect,
- The **structure** is the way tests are declared and discovered,
- The **runner** is the program that executes the tests.

OpenSavvy Prepared is a **structure** library: it concentrates on the way tests are declared.
It is possible to use other libraries (e.g. [Kotlin Test](https://kotlinlang.org/api/latest/kotlin.test/), [Kotest](https://kotest.io/), [Strikt](https://strikt.io/)…) to declare assertions.

[TOC]

## Features

- Lazy fixtures: Fixtures are executed lazily as they are needed, once per test
- Explicit fixtures
- Time is fixed during test execution
- Easy parametrization
- Coroutine-aware
- Multiplatform

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
