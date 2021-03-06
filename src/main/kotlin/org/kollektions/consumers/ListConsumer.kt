package org.kollektions.consumers

class ListConsumer<T>: Consumer<T> {
    var items = mutableListOf<T>()

    override  fun process(value: T) {
        items.add(value)
    }

    override  fun results() = items.toList()

    override  fun stop() {}
}

 fun<T> asList() = ListConsumer<T>()

 fun<T, V> ConsumerBuilder<T, V>.asList() = this.build(ListConsumer())
