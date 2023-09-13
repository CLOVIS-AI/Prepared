package opensavvy.prepared.runner.kotest

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class KotestVanillaTest : StringSpec({
	"hello world" {
		"hello".length shouldBe 5
	}
})
