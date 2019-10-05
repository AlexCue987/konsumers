package com.tgt.trans.common.examples.consumers

import com.tgt.trans.common.aggregator2.consumers.consume
import com.tgt.trans.common.aggregator2.consumers.sumOfInt
import com.tgt.trans.common.aggregator2.consumers.toSumOfBigDecimal
import com.tgt.trans.common.aggregator2.consumers.toSumOfLong
import com.tgt.trans.common.aggregator2.decorators.mapTo
import java.math.BigDecimal
import kotlin.test.assertEquals
import kotlin.test.Test

class SumExample {
    @Test
    fun `handles several items`() {
        val actual = listOf(1, 2).consume(
            sumOfInt(),
            mapTo { it:Int -> it.toLong() }.toSumOfLong(),
            mapTo { it:Int -> BigDecimal.valueOf(it.toLong()) }.toSumOfBigDecimal())

        assertEquals(listOf(3, 3L, BigDecimal.valueOf(3L)), actual)
    }
}
