package com.tgt.trans.common.aggregator2.decorators

import com.tgt.trans.common.aggregator2.consumers.Consumer
import com.tgt.trans.common.aggregator2.consumers.ConsumerBuilder

class MultipleStatesKeeper<T>(private val innerConsumer: Consumer<T>, val states: List<Consumer<T>>): Consumer<T> {
    constructor(innerConsumer: Consumer<T>,  vararg statesArgs: Consumer<T>) : this(innerConsumer, statesArgs.toList())

    override fun process(value: T) {
        innerConsumer.process(value)
        states.forEach { it.process(value) }
    }

    override fun results() = innerConsumer.results()

    fun states(): Sequence<Any> = states.asSequence().map { it.results() }
}

class MultipleStatesKeeperBuilder<T>(vararg statesArgs: Consumer<T>): ConsumerBuilder<T, T> {
    private val states = statesArgs.toList()

    override fun build(innerConsumer: Consumer<T>): Consumer<T> = MultipleStatesKeeper(innerConsumer, states)
}

fun<T> keepStates(vararg statesArgs: Consumer<T>) = MultipleStatesKeeperBuilder(*statesArgs)

//class ChainedStateKeeperBuilder<T, V>(private val previousBuilder: ConsumerBuilder<T, V>,
//                                      private val stateToKeep: Consumer<V>): ConsumerBuilder<T, V> {
//    override fun build(innerConsumer: Consumer<V>): Consumer<T> =
//        previousBuilder.build(StateKeeper(innerConsumer, stateToKeep))
//}
//
//fun<T, V> ConsumerBuilder<T, V>.keepState(stateToKeep: Consumer<V>) =
//    ChainedStateKeeperBuilder(this, stateToKeep)
