package com.tgt.trans.common.examples.consumers

import com.tgt.trans.common.aggregator2.consumers.asList
import com.tgt.trans.common.aggregator2.consumers.consume
import com.tgt.trans.common.aggregator2.decorators.filterOn
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
