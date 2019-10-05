package com.tgt.trans.common.examples.transformations

import com.tgt.trans.common.konsumers.consumers.asList
import com.tgt.trans.common.konsumers.consumers.consume
import com.tgt.trans.common.konsumers.transformations.filterOn
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
