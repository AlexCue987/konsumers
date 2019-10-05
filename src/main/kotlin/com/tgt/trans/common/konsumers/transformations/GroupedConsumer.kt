package com.tgt.trans.common.konsumers.transformations

import com.tgt.trans.common.konsumers.consumers.Consumer
import com.tgt.trans.common.konsumers.consumers.ConsumerBuilder

class GroupedConsumer<T, K>(private val keyFactory: (a: T) -> K,
                            private val innerConsumerFactory: () -> Consumer<T>) : Consumer<T> {
    private val groups = mutableMapOf<K, Consumer<T>>()

    override fun process(value: T) {
        val key = keyFactory(value)
        if (key !in groups) {
            groups[key] = innerConsumerFactory()
        }
        groups[key]!!.process(value)
    }

    override fun results() = groups.entries
            .associateBy({it.key}, {it.value.results()})
}

fun<T, K> groupBy(keyFactory: (a: T) -> K, innerConsumerFactory: () -> Consumer<T>) =
    GroupedConsumer(keyFactory, innerConsumerFactory)

fun<T, V, K> ConsumerBuilder<T, V>.groupBy(keyFactory: (a: V) -> K,
                                           innerConsumerFactory: () -> Consumer<V>) =
    this.build(GroupedConsumer(keyFactory, innerConsumerFactory))

