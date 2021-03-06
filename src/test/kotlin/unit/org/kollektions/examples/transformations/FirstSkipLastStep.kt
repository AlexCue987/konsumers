package org.kollektions.examples.transformations

import org.kollektions.consumers.asList
import org.kollektions.consumers.consume
import org.kollektions.transformations.*
import kotlin.test.Test
import kotlin.test.assertEquals

class FirstSkipLastStep {
    @Test
    fun `first skip last step`() {
        val actual = (0..10).asSequence()
            .consume(
                first<Int>(2).asList(),
                skip<Int>(8).asList(),
                last<Int>(2).asList(),
                step<Int>(4).asList(),
                skip<Int>(3).step(2).first(3).asList()
            )

        assertEquals(listOf(
            listOf(0, 1),
            listOf(8, 9, 10),
            listOf(9, 10),
            listOf(3, 7),
            listOf(4, 6, 8)),
            actual)
    }
}
