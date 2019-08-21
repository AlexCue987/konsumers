package com.tgt.trans.common.aggregator2.conditions

class FirstItemCondition<T>(private val condition: (firstValue: T, currentValue: T) -> Boolean): Condition<T> {
    private var firstValue: T? = null
    override fun get(value: T): Boolean {
        if (firstValue == null) {
            firstValue = value
        }
        return condition(firstValue!!, value)
    }

    fun getFirstValue() = firstValue

    override fun emptyCopy(): Condition<T> = FirstItemCondition(condition)
}

fun<T> firstItemCondition(condition: (firstValue: T, currentValue: T) -> Boolean) = FirstItemCondition(condition)

fun<T, V: Comparable<V>> sameProjectionAsFirst(projection: (currentValue: T) -> V) = firstItemCondition { a: T, b: T -> projection(a) == projection(b)}

fun<T, V: Comparable<V>> notSameProjectionAsFirst(projection: (currentValue: T) -> V) = firstItemCondition { a: T, b: T -> projection(a) != projection(b)}
