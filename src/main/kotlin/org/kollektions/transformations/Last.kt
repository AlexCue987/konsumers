package org.kollektions.transformations

import org.kollektions.consumers.Consumer
import org.kollektions.consumers.ConsumerBuilder

class Last<T>(private val count: Int, private val innerConsumer: Consumer<T>): Consumer<T> {
    var itemsProcessed = 0
    val buffer = Array<Any?>(count) { null }

    init {
        require(count > 0) {"Count must be positive, is: $count"}
    }

    override fun process(value: T) {
        buffer[itemsProcessed++ % count] = value
    }

    override fun results() = innerConsumer.results()

    override fun stop() {
        (maxOf(itemsProcessed - count, 0) until itemsProcessed)
            .forEach { innerConsumer.process(buffer[it % count] as T) }
        innerConsumer.stop()
    }
}

class LastBuilder<T>(val count: Int): ConsumerBuilder<T, T> {
    override fun build(innerConsumer: Consumer<T>): Consumer<T> = Last(count, innerConsumer)
}

class ChainedLastBuilder<T, V>(val previousBuilder: ConsumerBuilder<T, V>,
                               val count: Int): ConsumerBuilder<T, V> {
    override fun build(innerConsumer: Consumer<V>): Consumer<T> = previousBuilder.build(Last(count, innerConsumer))
}

fun<T> last(count: Int) = LastBuilder<T>(count)

fun<T, V> ConsumerBuilder<T, V>.last(count: Int): ConsumerBuilder<T, V> = ChainedLastBuilder(this, count)
