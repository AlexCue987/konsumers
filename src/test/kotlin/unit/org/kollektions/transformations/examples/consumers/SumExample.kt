package org.kollektions.transformations.examples.consumers

import org.kollektions.consumers.consume
import org.kollektions.consumers.sumOfInt
import org.kollektions.consumers.toSumOfBigDecimal
import org.kollektions.consumers.toSumOfLong
import org.kollektions.transformations.mapTo
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
