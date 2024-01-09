package opensavvy.prepared.compat.parameterize

import com.benwoodworth.parameterize.ExperimentalParameterizeApi
import com.benwoodworth.parameterize.ParameterizeScope.Parameter
import opensavvy.prepared.suite.Prepared
import opensavvy.prepared.suite.TestDsl
import opensavvy.prepared.suite.prepared

@ExperimentalParameterizeApi
fun <I, O> Parameter<I>.prepare(transform: suspend TestDsl.(I) -> O): Parameter<Prepared<O>> {
	return arguments
		.map { prepared(it.toString()) { transform(it) } }
		.let { Parameter(it) }
}
