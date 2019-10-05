package com.tgt.trans.common.examples.consumers

import com.tgt.trans.common.konsumers.consumers.Ratio2
import com.tgt.trans.common.konsumers.consumers.consume
import com.tgt.trans.common.konsumers.consumers.ratioOf
import kotlin.test.Test
import kotlin.test.assertEquals

class RatioOf {
    @Test
    fun `computes ratio`() {
        val actual = listOf(1, 2, 3).consume(ratioOf { it%2 == 0 })
        print(actual)
        assertEquals(Ratio2(conditionMet = 1L, outOf = 3L), actual[0])
    }
}
