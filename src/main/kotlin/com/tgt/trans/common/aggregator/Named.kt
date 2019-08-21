package com.tgt.trans.common.aggregator

class Named<T>(override val aggregator: Aggregator<T>,
                val name: String): DecoratedAggregator<T>{
    override fun process(value: T) = aggregator.process(value)

    override fun results() = NamedResults(name, aggregator.results())

    override fun emptyCopy() = Named(aggregator.emptyCopy(), name)
}

data class NamedResults<T>(val name: String, val results: T)

fun<T> Aggregator<T>.named(name: String) = Named(this, name)