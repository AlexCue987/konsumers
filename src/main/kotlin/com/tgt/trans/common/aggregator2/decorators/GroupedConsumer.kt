package com.tgt.trans.common.aggregator2.decorators

import com.tgt.trans.common.aggregator2.consumers.Consumer
import com.tgt.trans.common.aggregator2.consumers.ConsumerBuilder

class GroupedConsumer<T, K>(private val keyFactory: (a: T) -> K,
                            private val innerConsumer: Consumer<T>) : Consumer<T> {
    private val groups = mutableMapOf<K, Consumer<T>>()

    override fun process(value: T) {
        val key = keyFactory(value)
        if (key !in groups) {
            groups[key] = innerConsumer.emptyCopy()
        }
        groups[key]!!.process(value)
    }

    override fun results() = groups.entries
            .filter { !it.value.isEmpty() }
            .associateBy({it.key}, {it.value.results()})

    override fun emptyCopy(): Consumer<T> = GroupedConsumer(keyFactory, innerConsumer.emptyCopy())

    override fun isEmpty() = groups.values.all { it -> it.isEmpty() }
}

class GroupedConsumerBuilder<T, K>(val keyFactory: (a: T) -> K): ConsumerBuilder<T, T> {
    override fun build(innerConsumer: Consumer<T>): Consumer<T> = GroupedConsumer(keyFactory, innerConsumer)
}

fun<T, K> groupBy(keyFactory: (a: T) -> K) = GroupedConsumerBuilder(keyFactory)

class ChainedGroupedConsumerBuilder<T, K, V>(val previousBuilder: ConsumerBuilder<T, V>,
                                             val keyFactory: (a: V) -> K): ConsumerBuilder<T, V> {
    override fun build(innerConsumer: Consumer<V>): Consumer<T> =
            previousBuilder.build(GroupedConsumer(keyFactory, innerConsumer))
}

fun<T, K, V> ConsumerBuilder<T, V>.groupBy(keyFactory: (a: V) -> K) =
    ChainedGroupedConsumerBuilder(this, keyFactory)


