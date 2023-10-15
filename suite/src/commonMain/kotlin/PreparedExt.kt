package opensavvy.prepared.suite

/**
 * Creates a new [Prepared] which is the result of calling [block] on the input prepared value.
 *
 * @param name The name of the resulting prepared value.
 */
fun <I : Any, O : Any> Prepared<I>.map(name: String, block: (I) -> O): Prepared<O> =
	Prepared(name) { block(this@map()) }

/**
 * Creates a new [PreparedProvider] which is the result of calling [block] on the input prepared provider.
 */
fun <I : Any, O : Any> PreparedProvider<I>.map(block: (I) -> O): PreparedProvider<O> =
	PreparedProvider { block(this@map.block(this)) }
