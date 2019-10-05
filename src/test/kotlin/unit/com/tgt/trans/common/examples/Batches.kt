package com.tgt.trans.common.examples

import com.tgt.trans.common.konsumers.consumers.asList
import com.tgt.trans.common.konsumers.consumers.consume
import com.tgt.trans.common.konsumers.transformations.batches
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
