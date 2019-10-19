package com.tgt.trans.common.konsumers.consumers

import java.util.*

class Last<T: Any>: Consumer<T> {
    var itemsProcessed = 0
    lateinit var lastValue: T

    override fun process(value: T) {
        lastValue = value
        itemsProcessed++
    }

    override fun results() = if(itemsProcessed > 0) Optional.of(lastValue) else Optional.empty()

    override fun stop() {}
}

fun<T, V: Any> ConsumerBuilder<T, V>.last() = this.build(Last())
