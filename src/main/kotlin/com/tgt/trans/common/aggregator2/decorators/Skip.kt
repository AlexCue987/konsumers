package com.tgt.trans.common.aggregator2.decorators

import com.tgt.trans.common.aggregator2.consumers.Consumer
import com.tgt.trans.common.aggregator2.consumers.ConsumerBuilder

class Skip<T>(private val count: Int, private val innerConsumer: Consumer<T>): Consumer<T> {
    var itemsProcessed = 0

    init {
        require(count > 0) {"Count must be positive, is: $count"}
    }

    override fun process(value: T) {
        if (itemsProcessed++ >= count) {
            innerConsumer.process(value)
        }
    }

    override fun results() = innerConsumer.results()

    override fun stop() { innerConsumer.stop() }
}

class SkipBuilder<T>(val count: Int): ConsumerBuilder<T, T> {
    override fun build(innerConsumer: Consumer<T>): Consumer<T> = Skip(count, innerConsumer)
}

class ChainedSkipBuilder<T, V>(val previousBuilder: ConsumerBuilder<T, V>,
                                   val count: Int): ConsumerBuilder<T, V> {
    override fun build(innerConsumer: Consumer<V>): Consumer<T> = previousBuilder.build(Skip(count, innerConsumer))
}

fun<T> skip(count: Int) = SkipBuilder<T>(count)

fun<T, V> ConsumerBuilder<T, V>.skip(count: Int): ConsumerBuilder<T, V> = ChainedSkipBuilder(this, count)
