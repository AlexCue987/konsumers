package org.kollektions.examples.transformations

import org.kollektions.consumers.asList
import org.kollektions.consumers.consume
import org.kollektions.transformations.batches
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
