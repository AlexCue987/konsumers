package com.tgt.trans.common.aggregator

class ListOfAggregators<T>(vararg aggregatorsArgs: Aggregator<T>): Aggregator<T> {
    private val aggregators = aggregatorsArgs.toList()
    override fun emptyCopy(): Aggregator<T> = ListOfAggregators(*aggregators.map { it.emptyCopy() }.toTypedArray())

    override fun process(value: T) {
        aggregators.forEach { it.process(value) }
    }

    override fun results() = aggregators.map { it.results() }


    override fun isEmpty() = aggregators.all {it.isEmpty()}

    override fun stop() = aggregators.forEach { it.stop() }
}

fun<T> allOf(vararg aggregatorsArgs: Aggregator<T>) = ListOfAggregators(*aggregatorsArgs)