package opensavvy.prepared.suite.display

import opensavvy.prepared.runner.kotest.PreparedSpec

class DisplayTest : PreparedSpec({

	suite("Short") {
		test("Simple") {
			check(Display.Short.display("foo") == "foo")
		}

		test("Multiline") {
			val input = """
				First line
				Second line
				Third line
			""".trimIndent()

			check(Display.Short.display(input) == "First line / Second line / Third line")
		}

		test("Too long") {
			val input = """
				Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nulla a ligula faucibus metus vulputate viverra. Cras a tortor scelerisque, interdum sapien eu, bibendum risus. Vestibulum euismod ullamcorper risus, id consectetur lacus volutpat in. Suspendisse aliquet dolor in molestie lobortis. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aliquam erat volutpat. Cras non pulvinar ante. Mauris sed magna a purus vulputate vestibulum. Suspendisse scelerisque tincidunt lorem a maximus. Donec faucibus tellus massa, at ultrices magna efficitur vel. Suspendisse congue id quam posuere gravida. Aenean eleifend a purus quis pharetra. Sed aliquam erat mauris, sed facilisis est consequat non. Vivamus accumsan ligula eu lorem viverra mollis.

				Etiam vel eleifend nibh. Maecenas nec massa nec justo vestibulum vulputate. Aenean volutpat, orci quis ullamcorper luctus, diam velit placerat eros, ut sagittis ante magna nec nibh. Curabitur tristique urna scelerisque, dignissim nulla id, tempor felis. Nulla id ultrices magna, ac ultricies lacus. Donec scelerisque at lacus varius euismod. Proin id odio dui. Duis auctor id orci ac bibendum. Fusce suscipit urna purus, in varius sapien mollis sed. Phasellus vel nisi dui. Ut pharetra ligula ante, eget elementum turpis tristique vitae. Nam ut lorem quis ligula aliquet porttitor et eget mi.

				Aliquam erat volutpat. Duis volutpat dapibus risus. Phasellus sodales vestibulum eros in gravida. Nulla et lorem ac nulla maximus dignissim. Aliquam pulvinar neque vitae lorem hendrerit rhoncus. Nunc vel malesuada urna. Etiam sagittis, erat et hendrerit laoreet, dolor lorem malesuada nisi, a viverra nisl diam sed nunc. Etiam dapibus vitae nisl vel dictum. Quisque a egestas turpis.
			""".trimIndent()

			val expected = """
				Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nulla a ligula faucibus metus vulputate viverra. Cras a tortor scelerisque, interdum sapien eu, bibendum risus. Vestibulum euismod ullamcorper risus, id consectetur lacus volutpat in. Suspendisse ali… (pass Display.Full to view the full value)
			""".trimIndent()

			check(Display.Short.display(input) == expected)
		}
	}

	suite("Full") {
		val values = listOf(
			null,
			0,
			Float.POSITIVE_INFINITY,
			Int.MAX_VALUE,
			"null",
			"text",
			Char.MIN_HIGH_SURROGATE,
			listOf(156, "foo", null)
		)

		for (value in values) {
			test("Display $value") {
				check(Display.Full.display(value) == value.toString())
			}
		}
	}

})
