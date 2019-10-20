package com.tgt.trans.common.konsumers.consumers

import java.util.*

class Last<T: Any>: Consumer<T> {
    var itemsProcessed = 0
    lateinit var lastValue: T

    override inline fun process(value: T) {
        lastValue = value
        itemsProcessed++
    }

    override inline fun results() = if(itemsProcessed > 0) Optional.of(lastValue) else Optional.empty()

    override inline fun stop() {}
}

inline fun<T, V: Any> ConsumerBuilder<T, V>.last() = this.build(Last())
