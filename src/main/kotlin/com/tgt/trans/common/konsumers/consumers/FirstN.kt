package com.tgt.trans.common.konsumers.consumers

import java.util.*

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


fun<T, V> ConsumerBuilder<T, V>.firstN(count: Int) = this.build(FirstN<V>(count))

class First<T: Any>: Consumer<T> {
    var firstValue: Optional<T> = Optional.empty()

    override fun process(value: T) {
        if(!firstValue.isPresent) {
            firstValue = Optional.of(value)
        }
    }

    override fun results(): Any = firstValue
}

fun<T, V: Any> ConsumerBuilder<T, V>.first() = this.build(First())

class Last<T: Any>: Consumer<T> {
    var itemsProcessed = 0
    lateinit var lastValue: T

    override fun process(value: T) {
        lastValue = value
        itemsProcessed++
    }

    override fun results(): Any = if(itemsProcessed > 0) Optional.of(lastValue) else Optional.empty()
}
