package com.tgt.trans.common.examples

import com.tgt.trans.common.aggregator2.consumers.*
import com.tgt.trans.common.aggregator2.decorators.roundRobin
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
