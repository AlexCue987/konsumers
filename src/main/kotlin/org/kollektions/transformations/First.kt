package org.kollektions.transformations

import org.kollektions.consumers.Consumer
import org.kollektions.consumers.ConsumerBuilder

class First<T>(private val count: Int, private val innerConsumer: Consumer<T>): Consumer<T> {
    var itemsProcessed = 0

    init {
        require(count > 0) {"Count must be positive, is: $count"}
    }

    override fun process(value: T) {
        if (itemsProcessed++ < count) {
            innerConsumer.process(value)
        }
    }

    override fun results() = innerConsumer.results()

    override fun stop() { innerConsumer.stop() }
}

class FirstBuilder<T>(val count: Int): ConsumerBuilder<T, T> {
    override fun build(innerConsumer: Consumer<T>): Consumer<T> = First(count, innerConsumer)
}

class ChainedFirstBuilder<T, V>(val previousBuilder: ConsumerBuilder<T, V>,
                                val count: Int): ConsumerBuilder<T, V> {
    override fun build(innerConsumer: Consumer<V>): Consumer<T> = previousBuilder.build(First(count, innerConsumer))
}

fun<T> first(count: Int) = FirstBuilder<T>(count)

fun<T, V> ConsumerBuilder<T, V>.first(count: Int): ConsumerBuilder<T, V> = ChainedFirstBuilder(this, count)
