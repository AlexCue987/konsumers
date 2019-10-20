package com.tgt.trans.common.konsumers.consumers

import java.util.*

class First<T: Any>: Consumer<T> {
    var firstValue: Optional<T> = Optional.empty()

    override  fun process(value: T) {
        if(!firstValue.isPresent) {
            firstValue = Optional.of(value)
        }
    }

    override  fun results() = firstValue

    override  fun stop() {}
}

 fun<T, V: Any> ConsumerBuilder<T, V>.first() = this.build(First())
