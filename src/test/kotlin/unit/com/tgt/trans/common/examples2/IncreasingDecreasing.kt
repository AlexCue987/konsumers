package com.tgt.trans.common.examples2

import com.tgt.trans.common.aggregator2.conditions.decreasing
import com.tgt.trans.common.aggregator2.conditions.increasing
import com.tgt.trans.common.aggregator2.conditions.nonDecreasing
import com.tgt.trans.common.aggregator2.conditions.nonIncreasing
import com.tgt.trans.common.aggregator2.consumers.asList
import com.tgt.trans.common.aggregator2.consumers.consume
import org.junit.jupiter.api.assertAll
import kotlin.test.assertEquals
import kotlin.test.Test

class IncreasingDecreasing {

    @Test
    fun `increasing and decreasing`() {
        val actual = listOf(1, 2, 0, 0, 2, -1, 3).consume(
            increasing<Int>().asList(),
            nonIncreasing<Int>().asList(),
            decreasing<Int>().asList(),
            nonDecreasing<Int>().asList()
        )
        assertAll(
            { assertEquals(listOf(1, 2, 3), actual[0], "increasing") },
            { assertEquals(listOf(1, 0, 0, -1), actual[1], "nonIncreasing") },
            { assertEquals(listOf(1, 0, -1), actual[2], "decreasing") },
            { assertEquals(listOf(1, 2, 2, 3), actual[3], "nonDecreasing") }
        )
    }
}
