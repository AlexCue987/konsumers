package com.tgt.trans.common.konsumers.consumers

import java.util.*

class Min<T: Comparable<T>> : Consumer<T> {
    private var min = Optional.empty<T>()

    override fun process(value: T) {
        min = if (!min.isPresent || min.get() > value) Optional.of(value) else min
    }

    override fun results() = min
}

fun<T: Comparable<T>> min() = Min<T>()

fun<S, T: Comparable<T>> ConsumerBuilder<S, T>.min() = this.build(Min<T>())

class Max<T: Comparable<T>> : Consumer<T> {
    private var max = Optional.empty<T>()

    override fun process(value: T) {
        max = if (!max.isPresent || max.get() < value) Optional.of(value) else max
    }

    override fun results() = max
}

fun<T: Comparable<T>> max() = Max<T>()

fun<S, T: Comparable<T>> ConsumerBuilder<S, T>.max() = this.build(Max<T>())


