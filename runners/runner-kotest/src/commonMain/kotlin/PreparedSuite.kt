/*
 * Copyright (c) 2023-2026, OpenSavvy and contributors.
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

import io.kotest.common.KotestInternal
import io.kotest.core.spec.AbstractSpec
import io.kotest.core.spec.style.StringSpec
import io.kotest.core.spec.style.TestRunnable
import io.kotest.core.spec.style.scopes.RootScope
import opensavvy.prepared.runner.kotest.KotestSuiteDsl.Companion.suiteToTestDefinition
import opensavvy.prepared.suite.SuiteDsl
import opensavvy.prepared.suite.config.TestConfig

@OptIn(KotestInternal::class)
@Deprecated("The preparedSuite function's receiver has been changed from RootScope to AbstractSpec. Your code should not need to be updated. If you had a RootScope that isn't an AbstractSpec, please contact the Prepared maintainers.", level = DeprecationLevel.HIDDEN)
fun RootScope.preparedSuite(
	config: TestConfig = TestConfig.Empty,
	block: SuiteDsl.() -> Unit,
) {
	// To our knowledge, there are no RootScope instances that aren't also AbstractSpec instances, so this should be safe for any existing user.
	(this as AbstractSpec).preparedSuite(config, block)
}

/**
 * Executes a Prepared [SuiteDsl] in an existing Kotest suite.
 *
 * To create a Prepared-specific suite, see [PreparedSpec].
 *
 * ### Example
 *
 * This example uses [StringSpec], but tests can be registered using any spec.
 *
 * ```kotlin
 * class MyTests : StringSpec({
 *     "A regular Kotest test" {
 *         // …
 *     }
 *
 *     preparedSuite {
 *         test("A regular Prepared test") {
 *             // …
 *         }
 *
 *         suite("A regular Prepared test suite") {
 *             // …
 *         }
 *     }
 * })
 * ```
 */
@TestRunnable
fun AbstractSpec.preparedSuite(
	config: TestConfig = TestConfig.Empty,
	block: KotestSuiteDsl.() -> Unit,
) {
	add(
		suiteToTestDefinition(
			name = "Prepared",
			config = config,
			block = block,
		)
	)
}
