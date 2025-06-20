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

package opensavvy.prepared.suite

import opensavvy.prepared.runner.testballoon.preparedSuite
import opensavvy.prepared.suite.random.*

val RandomTest by preparedSuite {

	test("Generate random values without setting a seed") {
		check(random.nextInt() != 972016666)
		check(random.nextInt() != 1740578880)
		check(random.nextDouble() != 0.9049568172356872)
	}

	test("Setting the seed guarantees that the same sequence of numbers is generated") {
		random.setSeed(42)

		check(random.nextInt() == 972016666)
		check(random.nextInt() == 1740578880)
		check(random.nextDouble() == 0.9049568172356872)
		check(random.nextBoolean() == false)
	}

	val int1 by randomInt()
	val int2 by randomInt()
	val double by randomDouble()
	val boolean by randomBoolean()

	test("Setting the seed before accessing prepared values guarantees the sequence of numbers") {
		random.setSeed(43)

		check(int1() == -1828752340)
		check(int2() == -1728936224)
		check(double() == 0.9383535655576841)
		check(boolean() == false)
	}

}
