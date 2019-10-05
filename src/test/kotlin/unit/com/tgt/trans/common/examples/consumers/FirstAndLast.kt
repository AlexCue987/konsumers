package com.tgt.trans.common.examples.consumers

import com.tgt.trans.common.aggregator2.consumers.FirstN
import com.tgt.trans.common.aggregator2.consumers.LastN
import com.tgt.trans.common.aggregator2.consumers.consume
import kotlin.test.Test
import kotlin.test.assertEquals

class FirstAndLast {
    @Test
    fun `first and last N`() {
        val actual = (1..10).asSequence()
            .consume(FirstN(2), LastN(2))

        print(actual)

        assertEquals(listOf(listOf(1, 2), listOf(9, 10)), actual)
    }
}
