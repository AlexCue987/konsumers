package com.tgt.trans.common.konsumers.consumers

class Counter<T> : Consumer<T> {
    var counter = 0L

    override  fun process(value: T) { counter++ }

    override  fun results() = counter

    override  fun stop() {}
}

 fun<T, V> ConsumerBuilder<T, V>.count() = this.build(Counter())

 fun<T> count() = Counter<T>()
