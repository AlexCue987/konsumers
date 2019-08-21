package com.tgt.trans.common.aggregator2.decorators

import com.tgt.trans.common.aggregator2.consumers.Consumer
import com.tgt.trans.common.aggregator2.consumers.ConsumerBuilder

class RoundRobin<T>(private val count: Int,
                    private val consumer: Consumer<T>): Consumer<T> {

    init {
        require(count > 1) {"Count must be greater than one, is: $count"}
    }

    private val consumers = (1..count).asSequence()
        .map { consumer.emptyCopy() }
        .toList()

    private var index = 0

    override fun process(value: T) {
        consumers[index++ % count].process(value)
    }

    override fun results() = consumers.map { it.results() }

    override fun emptyCopy() = RoundRobin(count, consumer)

    override fun isEmpty() = consumers.all { it.isEmpty() }

    override fun stop() = consumers.forEach { it.stop() }
}


class ChainedRoundRobinBuilder<T, V>(private val previousBuilder: ConsumerBuilder<T, V>,
                                          private val count: Int): ConsumerBuilder<T, V> {
    override fun build(innerConsumer: Consumer<V>): Consumer<T> = previousBuilder.build(RoundRobin(count, innerConsumer))
}

class RoundRobinBuilder<T>(private val count: Int): ConsumerBuilder<T, T> {
    override fun build(innerConsumer: Consumer<T>): Consumer<T> = RoundRobin(count, innerConsumer)
}

fun<T, V> ConsumerBuilder<T, V>.roundRobin(count: Int) = ChainedRoundRobinBuilder(this, count)

fun<T> roundRobin(count: Int) = RoundRobinBuilder<T>(count)
