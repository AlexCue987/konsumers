package org.kollektions.consumers

import java.util.*

class Min<T: Comparable<T>> : Consumer<T> {
    var min = Optional.empty<T>()

    override  fun process(value: T) {
        min = if (!min.isPresent || min.get() > value) Optional.of(value) else min
    }

    override  fun results() = min

    override  fun stop() {}
}

 fun<T: Comparable<T>> min() = Min<T>()

 fun<S, T: Comparable<T>> ConsumerBuilder<S, T>.min() = this.build(Min<T>())

class Max<T: Comparable<T>> : Consumer<T> {
    var max = Optional.empty<T>()

    override  fun process(value: T) {
        max = if (!max.isPresent || max.get() < value) Optional.of(value) else max
    }

    override  fun results() = max

    override  fun stop() {}
}

 fun<T: Comparable<T>> max() = Max<T>()

 fun<S, T: Comparable<T>> ConsumerBuilder<S, T>.max() = this.build(Max<T>())



