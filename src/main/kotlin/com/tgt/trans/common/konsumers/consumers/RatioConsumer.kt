package com.tgt.trans.common.konsumers.consumers

class RatioCounter<T>(val condition: (a: T) -> Boolean) : Consumer<T> {
    var outOf = 0L
    var conditionMet = 0L

    override inline fun process(value: T) {
        outOf++
        if(condition(value)) {
            conditionMet++
        }
    }

    override inline fun results() = Ratio(conditionMet, outOf)

    override inline fun stop() {}
}

data class Ratio(val conditionMet: Long, val outOf: Long)

fun<T> ratioOf(condition: (a: T) -> Boolean) = RatioCounter(condition)

fun<T, V> ConsumerBuilder<T, V>.ratioOf(condition: (a: V) -> Boolean) = this.build(RatioCounter(condition))
