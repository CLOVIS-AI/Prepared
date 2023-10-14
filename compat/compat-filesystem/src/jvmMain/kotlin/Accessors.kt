package opensavvy.prepared.compat.filesystem

import opensavvy.prepared.suite.Prepared
import opensavvy.prepared.suite.PreparedProvider
import opensavvy.prepared.suite.map
import java.io.File
import java.nio.file.Path

// region java.io

/**
 * Accesses a file named [child] in the provided directory.
 */
operator fun File.div(child: String): File = File(this, child)

/**
 * Accesses a file named [child] in the provided directory.
 */
@JvmName("childFile")
operator fun Prepared<File>.div(child: String): Prepared<File> = map("$name / $child") { it / child }

/**
 * Accesses a file named [child] in the provided directory.
 */
@JvmName("childFile")
operator fun PreparedProvider<File>.div(child: String): PreparedProvider<File> = map { it / child }

// endregion
// region java.nio

/**
 * Accesses a file named [child] in the provided directory.
 */
operator fun Path.div(child: String): Path = resolve(child)

/**
 * Accesses a file named [child] in the provided directory.
 */
@JvmName("childPath")
operator fun Prepared<Path>.div(child: String): Prepared<Path> = map("$name / $child") { it / child }

/**
 * Accesses a file named [child] in the provided directory.
 */
@JvmName("childPath")
operator fun PreparedProvider<Path>.div(child: String): PreparedProvider<Path> = map { it / child }

// endregion
