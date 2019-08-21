package com.tgt.trans.common.aggregator

class GroupedAggregator<V, K> (private val keyFactory: (a: V) -> K,
                               private val aggregator: Aggregator<V>) : Aggregator<V> {
    override fun emptyCopy(): Aggregator<V> = GroupedAggregator(keyFactory, aggregator.emptyCopy())

    private val groups = mutableMapOf<K, Aggregator<V>>()

    override fun process(value: V) {
        val key = keyFactory(value)
        if (key !in groups) {
            groups[key] = aggregator.emptyCopy()
        }
        groups[key]!!.process(value)
    }

    override fun results() = groups.entries
            .filter { !it.value.isEmpty() }
            .associateBy({it.key}, {it.value.results()})

    override fun isEmpty() = groups.isEmpty() || groups.values.all { it.isEmpty() }

    override fun stop() = groups.values.forEach { it.stop() }
}

fun<V, K> Aggregator<V>.groupBy(keyFactory: (a: V) -> K) = GroupedAggregator<V, K>(keyFactory, this)