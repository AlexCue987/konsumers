package com.tgt.trans.common.konsumers.consumers

class FirstN<T>(private val count: Int): Consumer<T> {
    var itemsProcessed = 0
    val buffer = mutableListOf<T>()

    init {
        require(count > 0) {"Count must be positive, is: $count"}
    }

    override fun process(value: T) {
        if(itemsProcessed++ < count) {
            buffer.add(value)
        }
    }

    override fun results():List<T> = buffer
}