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

package opensavvy.prepared.runner.kti

import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.test.TestScope
import opensavvy.kti.*
import opensavvy.prepared.suite.SuiteDsl
import opensavvy.prepared.suite.TestDsl
import opensavvy.prepared.suite.config.Ignored
import opensavvy.prepared.suite.config.TestConfig
import opensavvy.prepared.suite.config.get
import opensavvy.prepared.suite.config.plus
import opensavvy.prepared.suite.runTestDslSuspend
import kotlin.coroutines.coroutineContext

@ExperimentalKtiApi
fun preparedSuite(@TestName name: String, block: SuiteDsl.() -> Unit): TestRoot =
	PreparedTestRoot(name).apply { discover(block) }

@ExperimentalKtiApi
private class PreparedTestRoot(
	private val name: String,
) : TestRoot {

	private val builder = PreparedTestNode(
		parentName = name,
		parentConfig = TestConfig.Empty,
	)

	fun discover(block: SuiteDsl.() -> Unit) {
		builder.block()
	}

	override suspend fun execute(context: TestCommand, reporter: TestReporter) {
		val suite = PreparedTestTree.Suite(
			name = name,
			config = TestConfig.Empty,
			children = builder.children,
		)

		suite.execute(context, reporter)
	}
}

@ExperimentalKtiApi
private class PreparedTestNode(
	private val parentName: String,
	private val parentConfig: TestConfig,
) : SuiteDsl {
	val children = ArrayList<PreparedTestTree>()

	override fun suite(
		name: String,
		config: TestConfig,
		block: SuiteDsl.() -> Unit,
	) {
		val fullName = "$parentName.$name"
		val fullConfig = parentConfig + config

		children += PreparedTestTree.Suite(
			name = fullName,
			config = fullConfig,
			children = PreparedTestNode(fullName, fullConfig)
				.apply { block() }
				.children
		)
	}

	override fun test(
		name: String,
		config: TestConfig,
		block: suspend TestDsl.() -> Unit,
	) {
		val fullName = "$parentName.$name"
		val fullConfig = parentConfig + config

		children += PreparedTestTree.Test(
			name = fullName,
			config = fullConfig,
		) {
			block()
		}
	}
}

@ExperimentalKtiApi
private sealed class PreparedTestTree {

	abstract val name: String
	abstract val config: TestConfig

	abstract suspend fun execute(
		context: TestCommand,
		reporter: TestReporter,
	)

	class Suite(
		override val name: String,
		override val config: TestConfig,
		val children: List<PreparedTestTree>,
	) : PreparedTestTree() {

		override suspend fun execute(context: TestCommand, reporter: TestReporter) {
			for (child in children) {
				child.execute(context, reporter)
			}
		}
	}

	class Test(
		override val name: String,
		override val config: TestConfig,
		val block: suspend TestDsl.() -> Unit,
	) : PreparedTestTree() {

		override suspend fun execute(context: TestCommand, reporter: TestReporter) {
			val shouldRun = !context.dryRun &&
				(context.includes.toList().isEmpty() || context.includes.any { it matches name }) &&
				context.excludes.none { it matches name } &&
				config[Ignored] == null

			if (!shouldRun) {
				reporter.report(object : TestEvent.Skipped {
					override val testName: String
						get() = name
				})
				return
			}

			reporter.report(object : TestEvent.Started {
				override val testName: String
					get() = name
			})
			try {
				val scope = TestScope(currentCoroutineContext())
				scope.runTestDslSuspend(name, config, block)

				reporter.report(object : TestEvent.Finished {
					override val testName: String
						get() = name
				})
			} catch (e: Exception) {
				coroutineContext.ensureActive()
				println("Failed with: $e") // In the future, report that in the test event somehow
				reporter.report(object : TestEvent.Failed {
					override val testName: String
						get() = name
				})
			}
		}
	}
}
