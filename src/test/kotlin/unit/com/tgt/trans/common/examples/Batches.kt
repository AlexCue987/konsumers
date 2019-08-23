package com.tgt.trans.common.examples

import com.tgt.trans.common.aggregator2.consumers.asList
import com.tgt.trans.common.aggregator2.consumers.consume
import com.tgt.trans.common.aggregator2.decorators.batches
import kotlin.test.Test
import kotlin.test.assertEquals

class Batches {
    @Test
    fun `splits into batches`() {
        val actual = listOf(1, 2, 3)
            .consume(
                batches<Int>(batchSize = 2).asList()
            )
        assertEquals(listOf(listOf(1, 2), listOf(3)), actual[0])
    }
}
