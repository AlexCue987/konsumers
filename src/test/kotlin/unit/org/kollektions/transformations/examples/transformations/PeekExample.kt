package org.kollektions.transformations.examples.transformations

import org.kollektions.consumers.asList
import org.kollektions.consumers.consume
import org.kollektions.transformations.peek
import kotlin.test.Test

class PeekExample {
    @Test
    fun `peek example`() {
        (0..3).asSequence().consume(
            peek<Int> { println("Processing item $it") }.asList()
        )
    }
}
