package org.kollektions.examples.consumers

import org.kollektions.consumers.asList
import org.kollektions.consumers.consume
import org.kollektions.transformations.filterOn
import kotlin.test.Test
import kotlin.test.assertEquals

class AsList {
    @Test
    fun `demonstrates asList`() {
        val actual = listOf(1, 2, 3)
            .consume(filterOn<Int> { it > 1 }.asList())
        assertEquals(listOf(2, 3), actual[0])
    }
}
