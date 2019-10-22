package org.kollektions.transformations.examples.consumers

import org.kollektions.consumers.*
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals

class FirstAndLast {
    @Test
    fun `first and last N`() {
        val actual = (1..10).asSequence()
            .consume(First(), Last(), FirstN(2), LastN(2))

        print(actual)

        assertEquals(listOf(Optional.of(1), Optional.of(10), listOf(1, 2), listOf(9, 10)), actual)
    }
}
