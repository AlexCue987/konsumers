package com.tgt.trans.common.konsumers.consumers

class ListConsumer<T>: Consumer<T> {
    var items = mutableListOf<T>()

    override inline fun process(value: T) {
        items.add(value)
    }

    override inline fun results() = items.toList()

    override inline fun stop() {}
}

inline fun<T> asList() = ListConsumer<T>()

inline fun<T, V> ConsumerBuilder<T, V>.asList() = this.build(ListConsumer())
