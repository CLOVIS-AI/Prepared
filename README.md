# OpenSavvy Playground

The Playground is a demonstrator project to showcase the multiple tools we create and how to use them.

Each demonstrator lives in a separate branch.
If you notice a demonstrator is out of date, please [create an issue](https://gitlab.com/opensavvy/playground/-/issues/new).


## Baseline

> Branch `baseline-demonstrator` | [Open in GitLab](https://gitlab.com/opensavvy/playground/-/tree/baseline-demonstrator)

The baseline demonstrator is an empty shell for OpenSavvy projects. In contains:
- IntelliJ configuration for our [coding style](https://gitlab.com/opensavvy/wiki/-/tree/main/coding-style)
- IntelliJ configuration for our [approved licenses](https://gitlab.com/opensavvy/wiki/-/tree/main/licensing)
- GitLab CI basic configuration

## Gradle

> Branch `gradle-demonstrator` | [Open in GitLab](https://gitlab.com/opensavvy/playground/-/tree/baseline-demonstrator)

Building upon the baseline, the Gradle demonstrator adds an empty "hello world" Kotlin project.

By default, the project uses:
- The version catalog
- Convention plugins to configure plugins in a centralized manner
- Gradle build cache
- Gradle configuration cache
- and more…

## Kotlin Fullstack (first-party)

> Branch `kotlin-fullstack-demonstrator` | [Open in GitLab](https://gitlab.com/opensavvy/playground/-/tree/kotlin-fullstack-demonstrator)

Building upon the Gradle demonstrator, this project adds a fullstack Kotlin example, with code shared between the client and server. This example strives to use only JetBrains-approved libraries to represent the "vanilla" Kotlin experience.

Technical stack:
- [Ktor](https://ktor.io/) — HTTP client and server
- [KotlinX.Serialization](https://github.com/Kotlin/kotlinx.serialization) — Multiplatform compile-time serialization library
- [KotlinX.Datetime](https://github.com/Kotlin/kotlinx-datetime) — Multiplatform datetime library 
- [KotlinX.Coroutines](https://github.com/Kotlin/kotlinx.coroutines) — Multiplatform asynchronous programming library with structured concurrency
- [Exposed](https://github.com/JetBrains/Exposed) — Kotlin SQL library 
- [Compose HTML](https://github.com/JetBrains/compose-multiplatform#compose-html) — DOM manipulation and state management library

## Kotlin Fullstack (OpenSavvy)

> Branch `kotlin-fullstack-opensavvy-demonstrator` | [Open in GitLab](https://gitlab.com/opensavvy/playground/-/tree/kotlin-fullstack-opensavvy-demonstrator)

Alternative version of the Kotlin Fullstack example, adding OpenSavvy-approved libraries to simplify the codebase and make it more performant.

Technical stack:
- Everything from the first-party variant
- [Arrow](https://arrow-kt.io/) — Explicit error management, functional-style
- [Pedestal](https://gitlab.com/opensavvy/pedestal) — Core utilities for multiplatform application development
  - [Progress](https://opensavvy.gitlab.io/pedestal/documentation/progress/index.html) — Implicit progress reporting
  - [State](https://opensavvy.gitlab.io/pedestal/documentation/state/index.html) — Progress-aware error management
  - [Cache](https://opensavvy.gitlab.io/pedestal/documentation/cache/index.html) — Asynchronous observable multiplatform cache algorithms to avoid useless requests
  - [Backbone](https://opensavvy.gitlab.io/pedestal/documentation/backbone/index.html) — Access-by-reference software pattern to aggressively cache operations throughout the entire stack
- [Decouple](https://gitlab.com/opensavvy/decouple) — Multiplatform UI framework
- [Vite](https://gitlab.com/opensavvy/gradle-vite-plugin) — Performant web bundler with faster and less intensive auto-reloading
