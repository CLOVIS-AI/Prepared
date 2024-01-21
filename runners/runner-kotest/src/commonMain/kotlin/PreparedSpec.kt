package opensavvy.prepared.runner.kotest

import io.kotest.core.spec.style.StringSpec
import opensavvy.prepared.suite.SuiteDsl

abstract class PreparedSpec(body: SuiteDsl.() -> Unit) : StringSpec({
	preparedSuite { body() }
})
