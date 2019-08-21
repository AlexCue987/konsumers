package com.tgt.trans.common.aggregator

import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*

class AvgAggregator<T, V> (private val adder: (a: T, b: T) -> T,
                        private val divider: (a: T, b: Int) -> V): Aggregator<T> {
    override fun emptyCopy(): Aggregator<T> = AvgAggregator(adder, divider)

    private var sum: T? = null

    private var count = 0

    override fun process(value: T) {
        sum = if (isEmpty()) value else adder(sum!!, value)
        count++
    }

    override fun results(): Optional<V> {
        return if (isEmpty()) Optional.empty()
        else Optional.of(divider(sum!!, count))
    }

    override fun isEmpty() = (count == 0)
}

fun avgOfIntAsBigDecimal(scaleMargin: Int = 2) = AvgAggregator(
        { a: Int, b: Int -> a + b },
        { a: Int, b: Int -> BigDecimal.valueOf(a.toLong()).divide(BigDecimal.valueOf(b.toLong()), scaleMargin, RoundingMode.HALF_EVEN) })

fun avgOfLongAsBigDecimal(scaleMargin: Int = 2) = AvgAggregator<Long, BigDecimal>(
        { a: Long, b: Long -> a + b },
        { a: Long, b: Int -> BigDecimal.valueOf(a).divide(BigDecimal.valueOf(b.toLong()), scaleMargin, RoundingMode.HALF_EVEN) })

fun avgOfBigDecimal(scaleMargin: Int = 2) = AvgAggregator(
        { a: BigDecimal, b: BigDecimal -> a + b },
        { a: BigDecimal, b: Int -> a.divide(BigDecimal.valueOf(b.toLong()), a.scale() + scaleMargin, RoundingMode.HALF_EVEN) })

fun avgOfDouble() = AvgAggregator ({ a: Double, b: Double -> a + b }, { a: Double, b: Int -> a / b })

fun<V> avgOfBigDecimal(projection: (a: V) -> BigDecimal) = ProjectedAggregator(
        projection = projection,
        aggregator = avgOfBigDecimal())

fun<V> avgOfDouble(projection: (a: V) -> Double) = ProjectedAggregator(
        projection = projection,
        aggregator = avgOfDouble())
