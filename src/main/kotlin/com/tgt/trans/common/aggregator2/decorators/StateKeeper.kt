package com.tgt.trans.common.aggregator2.decorators

import com.tgt.trans.common.aggregator2.consumers.Consumer
import com.tgt.trans.common.aggregator2.consumers.ConsumerBuilder

//class MultipleStatesKeeper<T>(private val innerConsumer: Consumer<T>, vararg statesArgs: Consumer<T>): Consumer<T> {
//    private val states = statesArgs.toList()
//
//    override fun process(value: T) {
//        innerConsumer.process(value)
//        states.forEach { it.process(value) }
//    }
//
//    override fun results() = innerConsumer.results()
//
//    fun states(): Sequence<Any> = states.asSequence().map { it.results() }
//}
//
//
//fun<T> keepStates(vararg aggregatorsArgs: Consumer<T>) = ListOfConsumers(*aggregatorsArgs)
//
//fun<S, T> ConsumerBuilder<S, T>.keepStates(vararg aggregatorsArgs: Consumer<T>) =
//    this.build(ListOfConsumers(*aggregatorsArgs))
//

class StateKeeper<T>(private val innerConsumer: Consumer<T>, private val stateToKeep: Consumer<T>): Consumer<T> {
    override fun process(value: T) {
        innerConsumer.process(value)
        stateToKeep.process(value)
    }

    override fun results() = innerConsumer.results()

    fun state() = stateToKeep.results()
}

class StateKeeperBuilder<T>(private val stateToKeep: Consumer<T>): ConsumerBuilder<T, T> {
    override fun build(innerConsumer: Consumer<T>): Consumer<T> = StateKeeper(innerConsumer, stateToKeep)

}

fun<T> keepState(stateToKeep: Consumer<T>) = StateKeeperBuilder(stateToKeep)


class ChainedStateKeeperBuilder<T, V>(private val previousBuilder: ConsumerBuilder<T, V>,
                                      private val stateToKeep: Consumer<V>): ConsumerBuilder<T, V> {
    override fun build(innerConsumer: Consumer<V>): Consumer<T> =
        previousBuilder.build(StateKeeper(innerConsumer, stateToKeep))
}

fun<T, V> ConsumerBuilder<T, V>.keepState(stateToKeep: Consumer<V>) =
    ChainedStateKeeperBuilder(this, stateToKeep)
