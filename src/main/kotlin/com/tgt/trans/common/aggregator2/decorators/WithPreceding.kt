package com.tgt.trans.common.aggregator2.decorators

import com.tgt.trans.common.aggregator2.consumers.Consumer
import com.tgt.trans.common.aggregator2.consumers.ConsumerBuilder
import java.lang.Integer.min

interface IWithPreceding<T> {
    fun value(): T
    fun preceding(offset: Int): T
    fun size(): Int
}

class WithPreceding<T>(val count: Int, val provideIncomplete: Boolean, val innerConsumer: Consumer<IWithPreceding<T>>)
    : Consumer<T>, IWithPreceding<T> {
    var itemsProcessed = 0
    val buffer = Array<Any?>(count) { null }

    init {
        require(count > 0) {"Count must be positive, is: $count"}
    }

    override fun process(value: T) {
        buffer[itemsProcessed++ % count] = value
        if (provideIncomplete || itemsProcessed >= count) {
            innerConsumer.process(this)
        }
    }

    override fun results() = innerConsumer.results()

    override fun value() = buffer[(itemsProcessed - 1) % count] as T

    override fun preceding(offset: Int): T {
        require(offset < itemsProcessed && offset < count)
        { "Offset must not exceed both itemsProcessed=$itemsProcessed and count=$count, is: $offset" }
        return buffer[(itemsProcessed - 1 - offset) % count] as T
    }

    override fun size() = min(count, itemsProcessed)

    override fun stop() {
        innerConsumer.stop()
    }
}

class WithPrecedingBuilder<T>(val count: Int, val provideIncomplete: Boolean): ConsumerBuilder<T, IWithPreceding<T>> {
    override fun build(innerConsumer: Consumer<IWithPreceding<T>>): Consumer<T> = WithPreceding(count, provideIncomplete, innerConsumer)
}

class ChainedWithPrecedingBuilder<T, V>(val previousBuilder: ConsumerBuilder<T, V>,
                               val count: Int,
                                        val provideIncomplete: Boolean): ConsumerBuilder<T, IWithPreceding<V>> {
    override fun build(innerConsumer: Consumer<IWithPreceding<V>>): Consumer<T> = previousBuilder.build(WithPreceding(count, provideIncomplete, innerConsumer))
}

fun<T> withPreceding(count: Int) = WithPrecedingBuilder<T>(count, false)

fun<T, V> ConsumerBuilder<T, V>.withPreceding(count: Int): ConsumerBuilder<T, IWithPreceding<V>> =
    ChainedWithPrecedingBuilder(this, count, false)

fun<T> toPairs(): ConsumerBuilder<T, Pair<T, T>> = withPreceding<T>(2).mapTo { Pair(it.preceding(1), it.value()) }

fun<S, T> ConsumerBuilder<S, T>.toPairs(): ConsumerBuilder<T, Pair<T, T>> = withPreceding<T>(2).mapTo { Pair(it.preceding(1), it.value()) }

fun<T> toRollingSeries(): ConsumerBuilder<T, List<T>> = withPreceding<T>(2).mapTo { s -> ((s.size() - 1) downTo 0).map { s.preceding(it) }.toList() }

fun<S, T> ConsumerBuilder<S, T>.toRollingSeries(): ConsumerBuilder<T, List<T>> = withPreceding<T>(2).mapTo { s -> ((s.size() - 1) downTo 0).map { s.preceding(it) }.toList() }

fun<T> withPrecedingIncomplete(count: Int) = WithPrecedingBuilder<T>(count, true)

fun<S, T> ConsumerBuilder<S, T>.withPrecedingIncomplete(count: Int): ConsumerBuilder<S, IWithPreceding<T>> =
    ChainedWithPrecedingBuilder(this, count, true)

fun<T> toRollingIncompleteSeries(): ConsumerBuilder<T, List<T>> = withPrecedingIncomplete<T>(2).mapTo { s -> ((s.size() - 1) downTo 0).map { s.preceding(it) }.toList() }

fun<S, T> ConsumerBuilder<S, T>.toRollingIncompleteSeries(): ConsumerBuilder<T, List<T>> = withPrecedingIncomplete<T>(2).mapTo { s -> ((s.size() - 1) downTo 0).map { s.preceding(it) }.toList() }
