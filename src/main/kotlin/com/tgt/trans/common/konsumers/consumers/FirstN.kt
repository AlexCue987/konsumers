package com.tgt.trans.common.konsumers.consumers

class FirstN<T>(val count: Int): Consumer<T> {
    var itemsProcessed = 0
    val buffer = mutableListOf<T>()

    init {
        require(count > 0) {"Count must be positive, is: $count"}
    }

    override inline fun process(value: T) {
        if(itemsProcessed++ < count) {
            buffer.add(value)
        }
    }

    override inline fun results():List<T> = buffer

    override inline fun stop() {}
}

inline fun<T, V> ConsumerBuilder<T, V>.firstN(count: Int) = this.build(FirstN<V>(count))

