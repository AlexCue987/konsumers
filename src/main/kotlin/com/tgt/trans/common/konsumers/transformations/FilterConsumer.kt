package com.tgt.trans.common.konsumers.transformations

import com.tgt.trans.common.konsumers.consumers.Consumer
import com.tgt.trans.common.konsumers.consumers.ConsumerBuilder

class FilterConsumer<T>(val filter: (value: T) -> Boolean, val innerConsumer: Consumer<T>) : Consumer<T> {

    override fun process(value: T) {
        if (filter(value)) {
            innerConsumer.process(value)
        }
    }

    override fun results() = innerConsumer.results()

    override fun stop() {
        innerConsumer.stop()
    }
}

class ChainedFilterConsumerBuilder<T, V>(val previousBuilder: ConsumerBuilder<T, V>, val filter: (value: V) -> Boolean): ConsumerBuilder<T, V> {
    override fun build(innerConsumer: Consumer<V>): Consumer<T> = previousBuilder.build(FilterConsumer(filter, innerConsumer))
}

class FilterConsumerBuilder<T>(val filter: (value: T) -> Boolean): ConsumerBuilder<T, T> {
    override fun build(innerConsumer: Consumer<T>): Consumer<T> = FilterConsumer(filter, innerConsumer)
}

fun<T, V> ConsumerBuilder<T, V>.filterOn(filter: (value: V) -> Boolean) = ChainedFilterConsumerBuilder(this, filter)

fun<T> filterOn(filter: (value: T) -> Boolean) = FilterConsumerBuilder(filter)
