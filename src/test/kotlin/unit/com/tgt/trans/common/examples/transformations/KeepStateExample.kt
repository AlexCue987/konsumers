package com.tgt.trans.common.examples.transformations

import com.tgt.trans.common.konsumers.consumers.asList
import com.tgt.trans.common.konsumers.consumers.consume
import com.tgt.trans.common.konsumers.consumers.max
import com.tgt.trans.common.konsumers.transformations.keepState
import com.tgt.trans.common.konsumers.transformations.peek
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
