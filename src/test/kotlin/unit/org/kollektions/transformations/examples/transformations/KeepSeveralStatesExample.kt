package org.kollektions.transformations.examples.transformations

import org.kollektions.consumers.asList
import org.kollektions.consumers.consume
import org.kollektions.consumers.max
import org.kollektions.consumers.min
import org.kollektions.transformations.keepStates
import org.kollektions.transformations.peek
import kotlin.test.Test
import kotlin.test.assertEquals

class KeepSeveralStatesExample {
    @Test
    fun `keepStates example`() {
        val minimum = min<Int>()
        val maximum = max<Int>()
        val numbers = listOf(2, 3, 1, 4)
        val actual = numbers.consume(
            keepStates(minimum, maximum)
                .peek { println("Processing item $it, minimum: ${minimum.results()}, maximum: ${maximum.results()}") }
                .asList()
        )

        assertEquals(numbers, actual[0], "Items are passed through peek unchanged")
    }
}
