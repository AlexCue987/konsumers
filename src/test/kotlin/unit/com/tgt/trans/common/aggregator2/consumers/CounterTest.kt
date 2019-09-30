package com.tgt.trans.common.aggregator2.consumers

import com.tgt.trans.common.aggregator2.consumers.consume
import com.tgt.trans.common.aggregator2.consumers.counter
import kotlin.test.assertEquals
import kotlin.test.Test

class CounterTest {
    @Test
    fun handlesEmpty() {
        val actual = listOf<Int>().consume(counter())
        assertEquals(0L, actual[0])
    }

    @Test
    fun handlesOneItem() {
        val actual = listOf(42).consume(counter())
        assertEquals(1L, actual[0])
    }
}
