package org.kollektions.transformations.examples.dispatchers

import org.kollektions.consumers.consume
import org.kollektions.consumers.max
import org.kollektions.consumers.min
import org.kollektions.dispatchers.allOf
import org.kollektions.transformations.filterOn
import org.kollektions.transformations.mapTo
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.Test

class AllOfExample {
    @Test
    fun `allOf after transformations`() {
        val actual = (1..10).asSequence()
            .consume(
                filterOn<Int> { it > 2 }
                    .mapTo { it * 2 }
                    .allOf(min(), max()))

        assertEquals(
            listOf(
                listOf(Optional.of(6), Optional.of(20))),
            actual)
    }
}
