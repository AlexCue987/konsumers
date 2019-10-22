package org.kollektions.examples.consumers

import org.kollektions.consumers.Ratio
import org.kollektions.consumers.consume
import org.kollektions.consumers.ratioOf
import kotlin.test.Test
import kotlin.test.assertEquals

class RatioOf {
    @Test
    fun `computes ratio`() {
        val actual = listOf(1, 2, 3).consume(ratioOf { it % 2 == 0 })
        print(actual)
        assertEquals(Ratio(conditionMet = 1L, outOf = 3L), actual[0])
    }
}
