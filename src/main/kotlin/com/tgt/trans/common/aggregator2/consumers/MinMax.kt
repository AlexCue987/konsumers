package com.tgt.trans.common.aggregator2.consumers

import java.util.*

class Min<T: Comparable<T>> : Consumer<T> {
    private var min = Optional.empty<T>()

    override fun process(value: T) {
        min = if (isEmpty() || min.get() > value) Optional.of(value) else min
    }

    override fun results() = min

    override fun emptyCopy() = Min<T>()

    override fun isEmpty() = !min.isPresent
}

fun<T: Comparable<T>> min() = Min<T>()

fun<S, T: Comparable<T>> ConsumerBuilder<S, T>.min() = this.build(Min<T>())

class Max<T: Comparable<T>> : Consumer<T> {
    private var max = Optional.empty<T>()

    override fun process(value: T) {
        max = if (isEmpty() || max.get() < value) Optional.of(value) else max
    }

    override fun results() = max

    override fun emptyCopy() = Max<T>()

    override fun isEmpty() = !max.isPresent
}

fun<T: Comparable<T>> max() = Max<T>()

fun<S, T: Comparable<T>> ConsumerBuilder<S, T>.max() = this.build(Max<T>())



