package opensavvy.prepared.runner.kotest

import io.kotest.core.spec.style.scopes.ContainerScope
import kotlinx.coroutines.await
import opensavvy.prepared.suite.TestDsl
import opensavvy.prepared.suite.config.TestConfig
import opensavvy.prepared.suite.runTestDsl
import kotlin.coroutines.CoroutineContext
import kotlin.js.Promise

internal actual suspend fun ContainerScope.executeTest(name: String, context: CoroutineContext, config: TestConfig, block: suspend TestDsl.() -> Unit) {
	// Currently, Kotest is not able to give us access to the Kotlin.Coroutines.Test dispatcher.
	// Instead, we create a new coroutine environment and awaits it.
	// See https://gitlab.com/opensavvy/groundwork/prepared/-/issues/59
	// See https://github.com/kotest/kotest/issues/4077

	val promise = runTestDsl(name, context, config, block) as Promise<*>
	promise.await()
}
