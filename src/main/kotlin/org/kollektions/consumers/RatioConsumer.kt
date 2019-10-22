package org.kollektions.consumers

class RatioCounter<T>(val condition: (a: T) -> Boolean) : Consumer<T> {
    var outOf = 0L
    var conditionMet = 0L

    override  fun process(value: T) {
        outOf++
        if(condition(value)) {
            conditionMet++
        }
    }

    override  fun results() = Ratio(conditionMet, outOf)

    override  fun stop() {}
}

data class Ratio(val conditionMet: Long, val outOf: Long)

fun<T> ratioOf(condition: (a: T) -> Boolean) = RatioCounter(condition)

fun<T, V> ConsumerBuilder<T, V>.ratioOf(condition: (a: V) -> Boolean) = this.build(RatioCounter(condition))
