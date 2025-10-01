/*
 * Copyright (c) 2025, OpenSavvy and contributors.
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

package opensavvy.prepared.suite.annotations

/**
 * Annotates functions that are test entrypoints.
 *
 * These functions accept a first `String` parameter that is the name of
 * the test or test suite.
 *
 * These functions usually accept a last parameter that is the body of the test.
 * It is assumed that a thrown exception means a test failure,
 * and no thrown exceptions means a test success.
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.BINARY)
@ExperimentalPreparedApi("https://gitlab.com/opensavvy/groundwork/prepared/-/issues/86")
annotation class TestEntrypoint
