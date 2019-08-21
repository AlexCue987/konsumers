package com.tgt.trans.common.aggregator

import java.math.BigDecimal
import java.util.*

class SumAggregator<T> (private val adder: (a: T, b: T) -> T): Aggregator<T> {
    override fun emptyCopy(): Aggregator<T> = SumAggregator(adder)

    private var sum: T? = null

    private var empty = true

    override fun process(value: T) {
        sum = if (empty) value else adder(sum!!, value)
        empty = false
    }

    override fun results(): Optional<T> {
        return if (empty) Optional.empty()
        else Optional.of(sum!!)
    }

    override fun isEmpty() = empty
}

fun sumOfInt() = SumAggregator { a: Int, b: Int -> a + b }

fun sumOfLong() = SumAggregator { a: Long, b: Long -> a + b }

fun sumOfBigDecimal() = SumAggregator { a: BigDecimal, b: BigDecimal -> a + b }

fun sumOfDouble() = SumAggregator { a: Double, b: Double -> a + b }

fun<V> sumOfInt(projection: (a: V) -> Int) = ProjectedAggregator(
        projection = projection,
        aggregator = SumAggregator { a: Int, b: Int -> a + b })

fun<V> sumOfBigDecimal(projection: (a: V) -> BigDecimal) = ProjectedAggregator(
        projection = projection,
        aggregator = SumAggregator { a: BigDecimal, b: BigDecimal -> a + b })

fun<V> sumOfDouble(projection: (a: V) -> Double) = ProjectedAggregator(
        projection = projection,
        aggregator = SumAggregator { a: Double, b: Double -> a + b })
