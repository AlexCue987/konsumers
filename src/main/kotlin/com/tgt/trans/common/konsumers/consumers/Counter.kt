package com.tgt.trans.common.konsumers.consumers

class Counter<T> : Consumer<T> {
    var counter = 0L

    override inline fun process(value: T) { counter++ }

    override inline fun results() = counter

    override inline fun stop() {}
}

inline fun<T, V> ConsumerBuilder<T, V>.count() = this.build(Counter())

inline fun<T> count() = Counter<T>()
