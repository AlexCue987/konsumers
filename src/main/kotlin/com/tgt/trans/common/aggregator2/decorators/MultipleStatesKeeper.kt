package com.tgt.trans.common.aggregator2.decorators

import com.tgt.trans.common.aggregator2.consumers.Consumer
import com.tgt.trans.common.aggregator2.consumers.ConsumerBuilder

class MultipleStatesKeeper<T>(private val innerConsumer: Consumer<T>,
                              private val states: List<Consumer<T>>): Consumer<T> {
    override fun process(value: T) {
        states.forEach { it.process(value) }
        innerConsumer.process(value)
    }

    override fun results() = innerConsumer.results()

    fun states(): Sequence<Any> = states.asSequence().map { it.results() }
}

class MultipleStatesKeeperBuilder<T>(vararg statesArgs: Consumer<T>): ConsumerBuilder<T, T> {
    private val states = statesArgs.toList()

    override fun build(innerConsumer: Consumer<T>): Consumer<T> = MultipleStatesKeeper(innerConsumer, states)
}

fun<T> keepStates(vararg statesArgs: Consumer<T>) = MultipleStatesKeeperBuilder(*statesArgs)

class ChainedMultipleStatesKeeperBuilder<T, V>(private val previousBuilder: ConsumerBuilder<T, V>,
                                               vararg statesArgs: Consumer<V>): ConsumerBuilder<T, V> {
    private val statesToKeep = statesArgs.toList()

    override fun build(innerConsumer: Consumer<V>): Consumer<T> =
        previousBuilder.build(MultipleStatesKeeper(innerConsumer, statesToKeep))
}

fun<T, V> ConsumerBuilder<T, V>.keepStates(vararg statesArgs: Consumer<V>) =
    ChainedMultipleStatesKeeperBuilder(this, *statesArgs)
