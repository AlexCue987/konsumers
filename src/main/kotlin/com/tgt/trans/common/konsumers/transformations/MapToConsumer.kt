package com.tgt.trans.common.konsumers.transformations

import com.tgt.trans.common.konsumers.consumers.Consumer
import com.tgt.trans.common.konsumers.consumers.ConsumerBuilder

class MapToConsumer<T, V>(val mapper: (a: T) -> V, val innerConsumer: Consumer<V>) : Consumer<T> {
    override fun process(value: T) {
        val mappedValue = mapper(value)
        innerConsumer.process(mappedValue)
    }

    override fun results() = innerConsumer.results()

    override fun stop() {
        innerConsumer.stop()
    }
}

class MapToConsumerBuilder<T, V>(val mapper: (a: T) -> V): ConsumerBuilder<T, V> {
    override fun build(innerConsumer: Consumer<V>): Consumer<T> = MapToConsumer(mapper, innerConsumer)
}

class ChainedMapToConsumerBuilder<T, V, W>(val previousBuilder: ConsumerBuilder<T, V>,
                                           val mapper: (a: V) -> W): ConsumerBuilder<T, W> {
    override fun build(innerConsumer: Consumer<W>): Consumer<T> = previousBuilder.build(MapToConsumer(mapper, innerConsumer))
}

fun<T, V> mapTo(mapper: (a: T) -> V) = MapToConsumerBuilder(mapper)

fun<T, V, W> ConsumerBuilder<T, V>.mapTo(mapper: (a: V) -> W): ConsumerBuilder<T, W> = ChainedMapToConsumerBuilder(this, mapper)
