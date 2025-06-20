/*
 * Copyright (c) 2024-2025, OpenSavvy and contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package opensavvy.prepared.runner.kotest

import io.kotest.core.spec.style.scopes.ContainerScope
import kotlinx.coroutines.await
import opensavvy.prepared.suite.TestDsl
import opensavvy.prepared.suite.config.TestConfig
import opensavvy.prepared.suite.runTestDsl
import kotlin.js.Promise

internal actual suspend fun ContainerScope.executeTest(name: String, config: TestConfig, block: suspend TestDsl.() -> Unit) {
	// Currently, Kotest is not able to give us access to the Kotlin.Coroutines.Test dispatcher.
	// Instead, we create a new coroutine environment and awaits it.
	// See https://gitlab.com/opensavvy/groundwork/prepared/-/issues/59
	// See https://github.com/kotest/kotest/issues/4077

	val promise = runTestDsl(name, config, block) as Promise<*>
	promise.await()
}
