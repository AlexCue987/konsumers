package com.tgt.trans.common.aggregator2.consumers

import kotlin.test.assertEquals
import kotlin.test.Test

class CounterTest {
    @Test
    fun handlesEmpty() {
        val actual = listOf<Int>().consume(count())
        assertEquals(0L, actual[0])
    }

    @Test
    fun handlesOneItem() {
        val actual = listOf(42).consume(count())
        assertEquals(1L, actual[0])
    }
}
