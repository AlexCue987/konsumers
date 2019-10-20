package com.tgt.trans.common.konsumers.consumers

import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*

class AvgConsumer<T, V> (val adder: (a: T, b: T) -> T,
                        val divider: (a: T, b: Int) -> V): Consumer<T> {
    var sum: T? = null

    var count = 0

    override inline fun process(value: T) {
        sum = if (isEmpty()) value else adder(sum!!, value)
        count++
    }

    override inline fun results(): Optional<V> {
        return if (isEmpty()) Optional.empty()
        else Optional.of(divider(sum!!, count))
    }

    override inline fun stop() {}

    inline fun isEmpty() = (count == 0)
}

inline fun avgOfInt(scaleMargin: Int = 2) = avgIntToDecimalConsumer(scaleMargin)

inline fun avgIntToDecimalConsumer(scaleMargin: Int): AvgConsumer<Int, BigDecimal> {
    return AvgConsumer(
        { a: Int, b: Int -> a + b },
        { a: Int, b: Int -> BigDecimal.valueOf(a.toLong()).divide(BigDecimal.valueOf(b.toLong()), scaleMargin, RoundingMode.HALF_EVEN) })
}

inline fun<T> ConsumerBuilder<T, Int>.avgOfInt(scaleMargin: Int = 2) = this.build(avgIntToDecimalConsumer(scaleMargin))

inline fun avgOfLong(scaleMargin: Int = 2) = avgLongToDecimalConsumer(scaleMargin)

inline fun avgLongToDecimalConsumer(scaleMargin: Int): AvgConsumer<Long, BigDecimal> {
    return AvgConsumer(
        { a: Long, b: Long -> a + b },
        { a: Long, b: Int -> BigDecimal.valueOf(a).divide(BigDecimal.valueOf(b.toLong()), scaleMargin, RoundingMode.HALF_EVEN) })
}

inline fun<T> ConsumerBuilder<T, Long>.avgOfLong(scaleMargin: Int = 2) = this.build(avgLongToDecimalConsumer(scaleMargin))


inline fun avgOfBigDecimal(scaleMargin: Int = 2) = avgOfBigDecimalConsumer(scaleMargin)

inline fun avgOfBigDecimalConsumer(scaleMargin: Int): AvgConsumer<BigDecimal, BigDecimal> {
    return AvgConsumer(
        { a: BigDecimal, b: BigDecimal -> a + b },
        { a: BigDecimal, b: Int -> a.divide(BigDecimal.valueOf(b.toLong()), a.scale() + scaleMargin, RoundingMode.HALF_EVEN) })
}

inline fun<T> ConsumerBuilder<T, BigDecimal>.avgOfBigDecimal(scaleMargin: Int = 2) =
    this.build(avgOfBigDecimalConsumer(scaleMargin))

