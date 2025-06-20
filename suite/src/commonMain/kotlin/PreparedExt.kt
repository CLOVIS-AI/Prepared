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

package opensavvy.prepared.suite

/**
 * Creates a new [Prepared] which is the result of calling [block] on the input prepared value.
 *
 * @param name The name of the resulting prepared value.
 */
fun <I, O> Prepared<I>.map(name: String, block: (I) -> O): Prepared<O> =
	Prepared(name, display) { block(this@map()) }

/**
 * Creates a new [PreparedProvider] which is the result of calling [block] on the input prepared provider.
 */
fun <I, O> PreparedProvider<I>.map(block: (I) -> O): PreparedProvider<O> =
	PreparedProvider(display) { block(this@map.block(this)) }
