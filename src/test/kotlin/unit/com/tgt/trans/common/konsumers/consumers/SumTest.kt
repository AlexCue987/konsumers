package com.tgt.trans.common.konsumers.consumers

import com.tgt.trans.common.konsumers.transformations.mapTo
import java.math.BigDecimal
import kotlin.test.assertEquals
import kotlin.test.Test

class SumTest {
    @Test
    fun handlesEmpty() {
        val actual = listOf<Int>().consume(sumOfInt())
        assertEquals(0, actual[0])
    }

    @Test
    fun handlesOneItem() {
        val actual = listOf(42).consume(sumOfInt())
        assertEquals(42, actual[0])
    }

    @Test
    fun `handles several items`() {
        val actual = listOf(1, 2).consume(
            sumOfInt(),
            mapTo { it:Int -> it.toLong() }.toSumOfLong(),
            mapTo { it:Int -> BigDecimal.valueOf(it.toLong()) }.toSumOfBigDecimal())

        assertEquals(listOf(3, 3L, BigDecimal.valueOf(3L)), actual)
    }
}
