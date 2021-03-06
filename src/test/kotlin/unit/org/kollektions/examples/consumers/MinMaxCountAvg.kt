package org.kollektions.examples.consumers

import org.kollektions.consumers.*
import org.kollektions.transformations.mapTo
import java.math.BigDecimal
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals

class MinMaxCountAvg {
    @Test
    fun iterateOnceGetSeveralResults() {
        val actual = (1..10).asSequence()
            .consume(min(),
                max(),
                count(),
                avgOfInt(),
                mapTo { it: Int -> it.toLong() }.avgOfLong(),
                mapTo { it: Int -> BigDecimal.valueOf(it.toLong()) }.avgOfBigDecimal()
                )

        print(actual)

        assertEquals(
            listOf(Optional.of(1),
                Optional.of(10),
                10L,
                Optional.of(BigDecimal("5.50")),
                Optional.of(BigDecimal("5.50")),
                Optional.of(BigDecimal("5.50"))
            ),
            actual)
    }
}
