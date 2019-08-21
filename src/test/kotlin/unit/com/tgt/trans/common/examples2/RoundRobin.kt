package com.tgt.trans.common.examples2

import com.tgt.trans.common.aggregator2.consumers.*
import com.tgt.trans.common.aggregator2.decorators.batches
import com.tgt.trans.common.aggregator2.decorators.filterOn
import com.tgt.trans.common.aggregator2.decorators.roundRobin
import io.mockk.*
import java.math.BigDecimal
import kotlin.test.*

class RoundRobin {
    @Test
    fun `invoke consumers in a round robin way`() {
        val items = listOf(42, 43, 44, 45, 46)
        val actual = items.consume(roundRobin<Int>(2).asList())

        assertEquals(listOf(listOf(42, 44, 46),
            listOf(43, 45)),
            actual[0])
    }
}
