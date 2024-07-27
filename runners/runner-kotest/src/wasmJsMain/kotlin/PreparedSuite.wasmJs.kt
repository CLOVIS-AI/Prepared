package opensavvy.prepared.runner.kotest

import io.kotest.core.coroutines.coroutineTestScope
import io.kotest.core.spec.style.scopes.ContainerScope
import opensavvy.prepared.suite.TestDsl
import opensavvy.prepared.suite.config.TestConfig
import opensavvy.prepared.suite.runTestDslSuspend
import kotlin.coroutines.CoroutineContext

internal actual suspend fun ContainerScope.executeTest(name: String, context: CoroutineContext, config: TestConfig, block: suspend TestDsl.() -> Unit) {
	coroutineTestScope.runTestDslSuspend(name, context, config, block)
}
