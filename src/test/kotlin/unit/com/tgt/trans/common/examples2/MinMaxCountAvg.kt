package com.tgt.trans.common.examples2

import com.tgt.trans.common.aggregator2.consumers.*
import java.math.BigDecimal
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals

class MinMaxCountAvg {
    @Test
    fun iterateOnceGetSeveralResults() {
        val actual = (1..10).asSequence()
            .consume(min2(),
                max2(),
                counter(),
                avgOfInt())

        assertEquals(
            listOf(Optional.of(1),
                Optional.of(10),
                10L,
                Optional.of(BigDecimal("5.50"))
            ),
            actual)
    }
}
