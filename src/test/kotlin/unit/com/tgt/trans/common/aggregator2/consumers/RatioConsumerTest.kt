package com.tgt.trans.common.aggregator2.consumers

import com.tgt.trans.common.aggregator2.decorators.mapTo
import java.math.BigDecimal
import kotlin.test.assertEquals
import kotlin.test.Test

class RatioConsumerTest {
    @Test
    fun handlesEmpty() {
        val actual = listOf<Int>().consume(ratioOf { a: Int -> a > 0 })
        assertEquals(Ratio2(0L, 0L), actual[0])
    }

    @Test
    fun handlesOneItemConditionNotMet() {
        val actual = listOf(-1).consume(ratioOf { a: Int -> a > 0 })
        assertEquals(Ratio2(0L, 1L), actual[0])
    }

    @Test
    fun handlesOneItemConditionMet() {
        val actual = listOf(1).consume(ratioOf { a: Int -> a > 0 })
        assertEquals(Ratio2(1L, 1L), actual[0])
    }

    @Test
    fun chainsWithBuilder() {
        val sut = mapTo { a: Int -> BigDecimal(a.toLong()).times(BigDecimal("0.5")) }
                .ratioOf {a: BigDecimal -> a > BigDecimal.ONE}
        val actual = listOf(1, 2, 3).consume(sut)
        assertEquals(Ratio2(1L, 3L), actual[0])
    }
}
