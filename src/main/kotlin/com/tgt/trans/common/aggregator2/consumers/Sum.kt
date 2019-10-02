package com.tgt.trans.common.aggregator2.consumers

import java.math.BigDecimal


class Sum<T>(initialValue: T,
                   private val aggregator: (a: T, b: T) -> T): Consumer<T> {
    private var aggregate: T = initialValue

    override fun process(value: T) {
        aggregate = aggregator(aggregate, value)
    }

    override fun results() = aggregate as Any

    fun sum() = aggregate
}

fun sumOfBigDecimal(): Sum<BigDecimal> = Sum(initialValue = BigDecimal.ZERO) { a: BigDecimal, b: BigDecimal -> a + b}
