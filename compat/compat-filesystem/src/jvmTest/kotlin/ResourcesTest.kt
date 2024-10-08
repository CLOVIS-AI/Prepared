package opensavvy.prepared.compat.filesystem

import opensavvy.prepared.compat.filesystem.pkg.PkgPackage
import opensavvy.prepared.compat.filesystem.resources.ExperimentalResourceApi
import opensavvy.prepared.compat.filesystem.resources.resource
import opensavvy.prepared.runner.kotest.PreparedSpec

@OptIn(ExperimentalResourceApi::class)
class ResourcesTest : PreparedSpec({

	suite("Read from different paths") {
		val fromCurrentClass by resource<ResourcesTest>("resource-text.txt")
			.read()

		val fromSubPackage by resource<PkgPackage>("test.txt")
			.read()

		val fromClassLoader by resource("root.txt", PkgPackage::class.java.classLoader)
			.read()

		test("Load resource from current package") {
			check(fromCurrentClass() == "From current package\n")
		}

		test("Load resource from another package") {
			check(fromSubPackage() == "From pkg package\n")
		}

		test("Load resource from root class loader") {
			check(fromClassLoader() == "From root\n")
		}
	}

})
