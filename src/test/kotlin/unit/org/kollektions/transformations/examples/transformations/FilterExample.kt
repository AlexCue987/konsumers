package org.kollektions.transformations.examples.transformations

import org.kollektions.consumers.asList
import org.kollektions.consumers.consume
import org.kollektions.transformations.filterOn
import kotlin.test.Test
import kotlin.test.assertEquals

class FilterExample {
    @Test
    fun `only even numbers`() {
        val actual = (1..5).asSequence().consume(
            filterOn<Int> { it%2 == 0 }.asList()
        )

        assertEquals(listOf(2, 4), actual[0])
    }
}
