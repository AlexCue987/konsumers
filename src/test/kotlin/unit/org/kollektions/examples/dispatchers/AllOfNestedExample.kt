package org.kollektions.examples.dispatchers

import org.kollektions.consumers.consume
import org.kollektions.consumers.max
import org.kollektions.consumers.min
import org.kollektions.dispatchers.allOf
import org.kollektions.transformations.filterOn
import org.kollektions.transformations.mapTo
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.Test

class AllOfNestedExample {
    @Test
    fun decorateConsumers() {
        val actual = (1..10).asSequence()
            .consume(
                filterOn<Int> { it > 2 }
                    .allOf(
                        mapTo<Int, Int> { it * 2 }
                            .allOf(min(), max()),
                        mapTo<Int, Int> { it * 3 }
                            .allOf(min(), max())
            ))

        assertEquals(
            listOf(
                listOf(Optional.of(6), Optional.of(20)),
                listOf(Optional.of(9), Optional.of(30))
            ),
            actual[0])
    }
}
