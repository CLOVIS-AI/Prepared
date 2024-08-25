package opensavvy.prepared.compat.filesystem.resources

import opensavvy.prepared.suite.PreparedProvider
import opensavvy.prepared.suite.prepared
import java.io.InputStream
import java.net.URL

internal interface ResourceLoader {
	fun resourceAsStream(path: String): InputStream?
	fun resourcePath(path: String): URL?
}

/**
 * Allow manipulating Java resources as part of a test.
 *
 * To obtain an instance of this class, see [resource].
 *
 * Resources provide special helpers to:
 * - [read] a resource.
 */
@ExperimentalResourceApi
class ResourceDesignator internal constructor(
	private val loader: ResourceLoader,
	private val path: String,
) {

	/**
	 * Allows reading a Java resource as a test fixture.
	 *
	 * ### Example
	 *
	 * ```kotlin
	 * private object CurrentFolder
	 *
	 * val test by resource<CurrentFolder>("test.txt")
	 *     .read()
	 *
	 * test("…") {
	 *     check(test() == "Hello world!\n")
	 * }
	 * ```
	 *
	 * To learn more about the way resources are loaded, see [resource].
	 */
	@ExperimentalResourceApi
	fun read(): PreparedProvider<String> =
		prepared {
			val stream = loader.resourceAsStream(path)
				?: error("Could not load resource ${this@ResourceDesignator}. Ensure the file is declared as a resource in the correct package.")

			stream.bufferedReader().use {
				it.readText()
			}
		}

	override fun toString(): String =
		"${loader.resourcePath(path)} (from path '$path')"
}

/**
 * Loads a Java resource named [path] from [loader].
 *
 * The resource will be accessed using [ClassLoader.getResource], [ClassLoader.getResourceAsStream], etc.
 * That is to say, the resource should be stored in the root package of the [loader].
 *
 * @see ResourceDesignator.read
 */
@ExperimentalResourceApi
fun resource(path: String, loader: ClassLoader) = ResourceDesignator(
	loader = object : ResourceLoader {
		override fun resourceAsStream(path: String): InputStream? =
			loader.getResourceAsStream(path)

		override fun resourcePath(path: String): URL? =
			loader.getResource(path)
	},
	path = path,
)

/**
 * Loads a Java resource named [path] from [loader].
 *
 * The resource will be accessed using [Class.getResource], [Class.getResourceAsStream], etc.
 * That is to say, the resource should be stored in the same package as the given class.
 *
 * ### Example
 *
 * For this code to work:
 * ```kotlin
 * // File TestClass.kt
 * package foo.bar.baz
 *
 * object TestClass
 *
 * val test by resource("test.txt", TestClass::class)
 *     .read()
 * ```
 * the `test.txt` file should be placed in the exact same package as the `TestClass`.
 * With typical Gradle configuration, the project may look like:
 * ```text
 * src/
 *   main/
 *     kotlin/
 *       foo/
 *         bar/
 *           baz/
 *             TestClass.kt
 *     resource/
 *       foo/
 *         bar/
 *           baz/
 *             test.txt
 * ```
 *
 * The overload that takes a single parameter is a shorthand for this function.
 *
 * @see ResourceDesignator.read
 */
@ExperimentalResourceApi
fun resource(path: String, loader: Class<*>) = ResourceDesignator(
	loader = object : ResourceLoader {
		override fun resourceAsStream(path: String): InputStream? =
			loader.getResourceAsStream(path)

		override fun resourcePath(path: String): URL? =
			loader.getResource(path)
	},
	path = path,
)

/**
 * Loads a Java resource named [path] that is in the same package as [TargetClass].
 *
 * ### Example
 *
 * ```kotlin
 * package foo.bar.baz
 *
 * object TargetClass
 *
 * val test by resource<TargetClass>("test.txt")
 *     .read()
 * ```
 * For this example to work, the file `test.txt` should be in the same package as the `TargetClass`, meaning
 * in the resources in subfolder `foo/bar/baz/test.txt`.
 *
 * To learn more about the location algorithm, see the overload that accepts a [Class].
 *
 * @see ResourceDesignator.read
 */
@ExperimentalResourceApi
inline fun <reified TargetClass : Any> resource(path: String) =
	resource(path, TargetClass::class.java)

@MustBeDocumented
@RequiresOptIn("Experimental API to access and modify resources. Visit https://gitlab.com/opensavvy/groundwork/prepared/-/issues/66 to give your opinion before it is stabilized.")
annotation class ExperimentalResourceApi
