package com.tgt.trans.common.aggregator

class ProjectedAggregator<V, T> (private val projection: (a: V) -> T,
                                private val aggregator: Aggregator<T>) : Aggregator<V> {
    override fun emptyCopy(): Aggregator<V> = ProjectedAggregator(projection, aggregator.emptyCopy())

    override fun process(value: V) {
        val projected = projection(value)
        aggregator.process(projected)
    }

    override fun results() = aggregator.results()


    override fun isEmpty() = aggregator.isEmpty()

    override fun stop() = aggregator.stop()
}

fun<V, T> Aggregator<T>.mapTo(projection: (a: V) -> T) = ProjectedAggregator(projection, this)
