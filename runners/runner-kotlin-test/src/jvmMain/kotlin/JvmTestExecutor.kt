/*
 * Copyright (c) 2023-2025, OpenSavvy and contributors.
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

package opensavvy.prepared.runner.kotlin

import opensavvy.prepared.suite.SuiteDsl
import opensavvy.prepared.suite.TestDsl
import opensavvy.prepared.suite.config.Ignored
import opensavvy.prepared.suite.config.TestConfig
import opensavvy.prepared.suite.config.get
import opensavvy.prepared.suite.config.plus
import opensavvy.prepared.suite.runTestDsl
import org.junit.jupiter.api.*
import java.util.stream.Stream

actual abstract class TestExecutor {

	actual open val config: TestConfig
		get() = TestConfig.Empty

	actual abstract fun SuiteDsl.register()

	@TestFactory
	fun suite(): Stream<out DynamicNode> {
		val suite = JvmSuiteDsl(config).apply { register() }

		return suite.nodes.stream()
	}

}

private class JvmSuiteDsl(val parentConfig: TestConfig) : SuiteDsl {
	val nodes = ArrayList<DynamicNode>()

	override fun suite(name: String, config: TestConfig, block: SuiteDsl.() -> Unit) {
		val child = JvmSuiteDsl(parentConfig + config).apply(block)

		nodes += DynamicContainer.dynamicContainer(name, child.nodes)
	}

	override fun test(name: String, config: TestConfig, block: suspend TestDsl.() -> Unit) {
		val thisConfig = parentConfig + config
		nodes += DynamicTest.dynamicTest(name) {
			// Immediately fail the test if it is marked as disabled
			Assumptions.assumeTrue(thisConfig[Ignored] == null)

			runTestDsl(name, thisConfig, block)
		}
	}
}
