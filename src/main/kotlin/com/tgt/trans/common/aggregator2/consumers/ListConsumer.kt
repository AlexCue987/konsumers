package com.tgt.trans.common.aggregator2.consumers

class ListConsumer<T>: Consumer<T> {
    override fun emptyCopy(): Consumer<T> = ListConsumer()

    private var items = mutableListOf<T>()

    override fun process(value: T) {
        items.add(value)
    }

    override fun results() = items.toList()

    override fun isEmpty() = items.isEmpty()
}

fun<T> asList() = ListConsumer<T>()

fun<T, V> ConsumerBuilder<T, V>.asList() = this.build(ListConsumer())
