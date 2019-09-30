package com.tgt.trans.common.aggregator2.decorators

import com.tgt.trans.common.aggregator2.consumers.Consumer
import com.tgt.trans.common.aggregator2.consumers.ConsumerBuilder

class Peek<T>(private val action: (a: T) -> Unit, private val innerConsumer: Consumer<T>): Consumer<T> {

    override fun process(value: T) {
        action(value)
        innerConsumer.process(value)
    }

    override fun results() = innerConsumer.results()

    override fun stop() { innerConsumer.stop() }
}

class PeekBuilder<T>(val action: (a: T) -> Unit): ConsumerBuilder<T, T> {
    override fun build(innerConsumer: Consumer<T>): Consumer<T> = Peek(action, innerConsumer)
}

class ChainedPeekBuilder<T, V>(val previousBuilder: ConsumerBuilder<T, V>,
                                   val action: (a: V) -> Unit): ConsumerBuilder<T, V> {
    override fun build(innerConsumer: Consumer<V>): Consumer<T> = previousBuilder.build(Peek(action, innerConsumer))
}

fun<T> peek(action: (a: T) -> Unit) = PeekBuilder<T>(action)

fun<T, V> ConsumerBuilder<T, V>.peek(action: (a: V) -> Unit): ConsumerBuilder<T, V> = ChainedPeekBuilder(this, action)
