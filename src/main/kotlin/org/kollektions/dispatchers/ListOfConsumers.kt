package org.kollektions.dispatchers

import org.kollektions.consumers.Consumer
import org.kollektions.consumers.ConsumerBuilder

class ListOfConsumers<T>(vararg consumersArgs: Consumer<T>): Consumer<T> {
    private val consumers = consumersArgs.toList()

    override fun process(value: T) {
        consumers.forEach { it.process(value) }
    }

    override fun results() = consumers.map { it.results() }

    override fun stop() = consumers.forEach { it.stop() }
}

fun<T> allOf(vararg aggregatorsArgs: Consumer<T>) = ListOfConsumers(*aggregatorsArgs)

fun<S, T> ConsumerBuilder<S, T>.allOf(vararg aggregatorsArgs: Consumer<T>) =
        this.build(ListOfConsumers(*aggregatorsArgs))
