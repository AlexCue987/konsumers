package org.kollektions.transformations

import org.kollektions.consumers.Consumer
import org.kollektions.consumers.ConsumerBuilder

class StateKeeper<T>(private val innerConsumer: Consumer<T>, private val stateToKeep: Consumer<T>): Consumer<T> {
    override fun process(value: T) {
        stateToKeep.process(value)
        innerConsumer.process(value)
    }

    override fun results() = innerConsumer.results()

    override fun stop() = innerConsumer.stop()
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
