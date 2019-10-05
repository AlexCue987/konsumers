package com.tgt.trans.common.examples.consumers

import com.tgt.trans.common.konsumers.consumers.consume
import com.tgt.trans.common.konsumers.consumers.sumOfInt
import com.tgt.trans.common.konsumers.consumers.toSumOfBigDecimal
import com.tgt.trans.common.konsumers.consumers.toSumOfLong
import com.tgt.trans.common.konsumers.transformations.mapTo
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
