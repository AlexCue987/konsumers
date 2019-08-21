package com.tgt.trans.common.examples2

import com.tgt.trans.common.aggregator2.consumers.Ratio2
import com.tgt.trans.common.aggregator2.consumers.consume
import com.tgt.trans.common.aggregator2.consumers.ratioOf
import kotlin.test.Test
import kotlin.test.assertEquals

class RatioOf {
    @Test
    fun `computes ratio`() {
        val actual = listOf(1, 2, 3).consume(ratioOf { it%2 == 0 })
        assertEquals(Ratio2(conditionMet = 1L, outOf = 3L), actual[0])
    }
}
