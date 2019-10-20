package com.tgt.trans.common.konsumers.consumers

import java.util.*

class Min<T: Comparable<T>> : Consumer<T> {
    var min = Optional.empty<T>()

    override inline fun process(value: T) {
        min = if (!min.isPresent || min.get() > value) Optional.of(value) else min
    }

    override inline fun results() = min

    override inline fun stop() {}
}

inline fun<T: Comparable<T>> min() = Min<T>()

inline fun<S, T: Comparable<T>> ConsumerBuilder<S, T>.min() = this.build(Min<T>())

class Max<T: Comparable<T>> : Consumer<T> {
    var max = Optional.empty<T>()

    override inline fun process(value: T) {
        max = if (!max.isPresent || max.get() < value) Optional.of(value) else max
    }

    override inline fun results() = max

    override inline fun stop() {}
}

inline fun<T: Comparable<T>> max() = Max<T>()

inline fun<S, T: Comparable<T>> ConsumerBuilder<S, T>.max() = this.build(Max<T>())



