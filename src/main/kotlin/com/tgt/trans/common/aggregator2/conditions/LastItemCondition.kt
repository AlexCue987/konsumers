package com.tgt.trans.common.aggregator2.conditions

import com.tgt.trans.common.aggregator2.decorators.filterOn

class LastItemCondition<T>(private val condition: (previousValue: T, currentValue: T) -> Boolean,
                           private val firstItemEvaluation: FirstItemEvaluation = FirstItemEvaluation.True): Condition<T> {
    private var lastValue: T? = null
    override fun get(value: T): Boolean {
        if (lastValue == null) {
            lastValue = value
            return evaluateForFirstItem(value)
        }
        val previousValue = lastValue!!
        val meetsCondition = condition(previousValue, value)
        if(meetsCondition) {
            lastValue = value
        }
        return meetsCondition
    }

    fun evaluateForFirstItem(value: T) = when(firstItemEvaluation) {
        FirstItemEvaluation.True -> true
        FirstItemEvaluation.False -> false
        else -> condition(value, value)
    }

    override fun emptyCopy(): Condition<T> = FirstItemCondition(condition)
}

enum class FirstItemEvaluation { True, False, Evaluate }

fun<T> lastItemCondition(condition: (previousValue: T, currentValue: T) -> Boolean, firstItemEvaluation: FirstItemEvaluation = FirstItemEvaluation.True) =
    LastItemCondition(condition, firstItemEvaluation)

fun<T: Comparable<T>> increasing() = filterOn(LastItemCondition(condition = { a: T, b: T -> a < b} ))

fun <T: Comparable<T>> nonIncreasing() = filterOn(LastItemCondition(condition =  { a: T, b: T -> a >= b}))

fun<T: Comparable<T>> decreasing() = filterOn(LastItemCondition(condition =  { a: T, b: T -> a > b}))

fun<T: Comparable<T>> nonDecreasing() = filterOn(LastItemCondition(condition =  { a: T, b: T -> a <= b}))

