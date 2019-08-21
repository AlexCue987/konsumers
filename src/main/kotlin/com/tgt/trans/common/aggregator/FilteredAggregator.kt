package com.tgt.trans.common.aggregator

class FilteredAggregator<T> (override val aggregator: Aggregator<T>,
                             private val filter: (a: T) -> Boolean
                                ) : DecoratedAggregator<T> {
    override fun emptyCopy(): Aggregator<T> = FilteredAggregator(aggregator.emptyCopy(), filter)

    override fun process(value: T) {
        if (filter(value)) {
            aggregator.process(value)
        }
    }
}

fun<V> Aggregator<V>.filterOn(filter: (a: V) -> Boolean) = FilteredAggregator(this, filter)
