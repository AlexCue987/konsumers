package com.tgt.trans.common.aggregator2.consumers

class Counter<T> : Consumer<T> {
    var counter = 0L

    override fun process(value: T) { counter++ }

    override fun results() = counter

    override fun emptyCopy() = Counter<T>()

    override fun isEmpty() = counter == 0L
}

fun<T, V> ConsumerBuilder<T, V>.count() = this.build(Counter())

fun<T> counter() = Counter<T>()
