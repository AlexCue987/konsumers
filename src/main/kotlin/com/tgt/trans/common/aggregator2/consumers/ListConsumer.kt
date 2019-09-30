package com.tgt.trans.common.aggregator2.consumers

class ListConsumer<T>: Consumer<T> {
    private var items = mutableListOf<T>()

    override fun process(value: T) {
        items.add(value)
    }

    override fun results() = items.toList()
}

fun<T> asList() = ListConsumer<T>()

fun<T, V> ConsumerBuilder<T, V>.asList() = this.build(ListConsumer())
