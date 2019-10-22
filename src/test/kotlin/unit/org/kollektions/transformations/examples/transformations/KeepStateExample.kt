package org.kollektions.transformations.examples.transformations

import org.kollektions.consumers.asList
import org.kollektions.consumers.consume
import org.kollektions.consumers.max
import org.kollektions.transformations.keepState
import org.kollektions.transformations.peek
import kotlin.test.Test
import kotlin.test.assertEquals

class KeepStateExample {
    @Test
    fun `keepState example`() {
        val maximum = max<Int>()
        val numbers = listOf(1, 3, 2, 4)
        val actual = numbers.consume(
            keepState(maximum)
                .peek { println("Processing item $it, state: ${maximum.results()}") }
                .asList()
        )

        assertEquals(numbers, actual[0], "Items are passed through peek unchanged")
    }
}
