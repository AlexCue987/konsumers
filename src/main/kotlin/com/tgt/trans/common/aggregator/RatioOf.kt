package com.tgt.trans.common.aggregator

class RatioOf<T> (private val condition: (a: T) -> Boolean,
                  private val resultsFactory: (a: Long, b: Long) -> Any): Aggregator<T> {

    private var total = 0L
    private var totalWhenConditionMet = 0L

    override fun process(value: T) {
        total++
        if(condition(value)) {
            totalWhenConditionMet ++
        }
    }

    override fun results() = resultsFactory(totalWhenConditionMet, total)

    override fun emptyCopy() = RatioOf(condition, resultsFactory)

    override fun isEmpty() = total == 0L
}

data class Ratio(val conditionMet: Long, val outOf: Long)

fun<T> ratio(condition: (a: T) -> Boolean) = RatioOf(condition) { a: Long, b: Long -> Ratio(a, b) }

fun<T> always(condition: (a: T) -> Boolean) = RatioOf(condition) { a: Long, b: Long -> a == b }

fun<T> sometimes(condition: (a: T) -> Boolean) = RatioOf(condition) { a: Long, b: Long -> a > 0L || b == 0L}

fun<T> never(condition: (a: T) -> Boolean) = RatioOf(condition) { a: Long, b: Long -> a == 0L || b == 0L }
