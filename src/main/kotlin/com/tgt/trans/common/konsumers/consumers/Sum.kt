package com.tgt.trans.common.konsumers.consumers

import java.math.BigDecimal


class Sum<T>(initialValue: T,
                   val aggregator: (a: T, b: T) -> T): Consumer<T> {
    var aggregate: T = initialValue

    override  fun process(value: T) {
        aggregate = aggregator(aggregate, value)
    }

    override  fun results() = aggregate as Any

     fun sum(): T {
        return aggregate
    }

    override  fun stop() {}
}

fun sumOfBigDecimal(): Sum<BigDecimal> = Sum(initialValue = BigDecimal.ZERO) { a: BigDecimal, b: BigDecimal -> a + b}

fun sumOfLong(): Sum<Long> = Sum(initialValue = 0L) { a: Long, b: Long -> a + b}

fun sumOfInt(): Sum<Int> = Sum(initialValue = 0) { a: Int, b: Int -> a + b}

fun<T> ConsumerBuilder<T, BigDecimal>.toSumOfBigDecimal() = this.build(sumOfBigDecimal())

fun<T> ConsumerBuilder<T, Long>.toSumOfLong() = this.build(sumOfLong())

fun<T> ConsumerBuilder<T, Int>.toSumOfInt() = this.build(sumOfInt())
