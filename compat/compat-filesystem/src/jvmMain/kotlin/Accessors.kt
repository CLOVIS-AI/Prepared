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
operator fun Prepared<File>.div(child: String): Prepared<File> = map("$name/$child") { it / child }

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
operator fun Prepared<Path>.div(child: String): Prepared<Path> = map("$name/$child") { it / child }

/**
 * Accesses a file named [child] in the provided directory.
 */
@JvmName("childPath")
operator fun PreparedProvider<Path>.div(child: String): PreparedProvider<Path> = map { it / child }

// endregion
