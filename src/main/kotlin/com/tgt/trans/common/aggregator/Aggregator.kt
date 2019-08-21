package com.tgt.trans.common.aggregator

interface Aggregator<T> {
    fun process(value: T)
    fun results(): Any
    fun emptyCopy(): Aggregator<T>
    fun isEmpty(): Boolean
    fun stop() {}
 }

interface DecoratedAggregator<T>: Aggregator<T> {
    val aggregator: Aggregator<T>
    override fun results() = aggregator.results()
    override fun isEmpty() = aggregator.isEmpty()
    override fun stop() = aggregator.stop()
}

fun <T> Iterable<T>.aggregate(vararg aggregators: Aggregator<T>): List<Any> {
    val aggregatorsList = aggregators.toList()
    val iterator = iterator()
    while (iterator.hasNext()) {
        val value = iterator.next()
        aggregatorsList.forEach { it.process(value) }
    }
    aggregatorsList.forEach { it.stop() }
    return aggregatorsList.map {it.results()}
}
