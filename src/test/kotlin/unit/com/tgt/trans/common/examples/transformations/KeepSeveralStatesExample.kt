package com.tgt.trans.common.examples.transformations

import com.tgt.trans.common.konsumers.consumers.asList
import com.tgt.trans.common.konsumers.consumers.consume
import com.tgt.trans.common.konsumers.consumers.max
import com.tgt.trans.common.konsumers.consumers.min
import com.tgt.trans.common.konsumers.transformations.keepState
import com.tgt.trans.common.konsumers.transformations.keepStates
import com.tgt.trans.common.konsumers.transformations.peek
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
