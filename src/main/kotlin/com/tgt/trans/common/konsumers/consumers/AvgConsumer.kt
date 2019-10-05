package com.tgt.trans.common.konsumers.consumers

import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*

class AvgConsumer<T, V> (private val adder: (a: T, b: T) -> T,
                        private val divider: (a: T, b: Int) -> V): Consumer<T> {
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

    fun isEmpty() = (count == 0)
}

fun avgOfInt(scaleMargin: Int = 2) = avgIntToDecimalConsumer(scaleMargin)

private fun avgIntToDecimalConsumer(scaleMargin: Int): AvgConsumer<Int, BigDecimal> {
    return AvgConsumer(
        { a: Int, b: Int -> a + b },
        { a: Int, b: Int -> BigDecimal.valueOf(a.toLong()).divide(BigDecimal.valueOf(b.toLong()), scaleMargin, RoundingMode.HALF_EVEN) })
}

fun<T> ConsumerBuilder<T, Int>.avgOfInt(scaleMargin: Int = 2) = this.build(avgIntToDecimalConsumer(scaleMargin))

fun avgOfLong(scaleMargin: Int = 2) = avgLongToDecimalConsumer(scaleMargin)

private fun avgLongToDecimalConsumer(scaleMargin: Int): AvgConsumer<Long, BigDecimal> {
    return AvgConsumer(
        { a: Long, b: Long -> a + b },
        { a: Long, b: Int -> BigDecimal.valueOf(a).divide(BigDecimal.valueOf(b.toLong()), scaleMargin, RoundingMode.HALF_EVEN) })
}

fun<T> ConsumerBuilder<T, Long>.avgOfLong(scaleMargin: Int = 2) = this.build(avgLongToDecimalConsumer(scaleMargin))


fun avgOfBigDecimal(scaleMargin: Int = 2) = avgOfBigDecimalConsumer(scaleMargin)

private fun avgOfBigDecimalConsumer(scaleMargin: Int): AvgConsumer<BigDecimal, BigDecimal> {
    return AvgConsumer(
        { a: BigDecimal, b: BigDecimal -> a + b },
        { a: BigDecimal, b: Int -> a.divide(BigDecimal.valueOf(b.toLong()), a.scale() + scaleMargin, RoundingMode.HALF_EVEN) })
}

fun<T> ConsumerBuilder<T, BigDecimal>.avgOfBigDecimal(scaleMargin: Int = 2) =
    this.build(avgOfBigDecimalConsumer(scaleMargin))

