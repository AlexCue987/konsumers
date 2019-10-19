package com.tgt.trans.common.konsumers.consumers

import com.tgt.trans.common.konsumers.transformations.filterOn
import kotlin.test.assertEquals
import kotlin.test.Test

class ListConsumerTest {
    @Test
    fun handlesEmpty() {
        val actual = listOf<Int>().consume(
            asList(),
            filterOn { a: Int -> a > 0 }.asList())
        assertEquals(listOf(emptyList<Int>(), emptyList()), actual)
    }

    @Test
    fun handlesOneItem() {
        val answer = 42
        val actual = listOf(answer).consume(asList(),
            filterOn { a: Int -> a > 0 }.asList())
        assertEquals(listOf(listOf(answer), listOf(answer)), actual)
    }

    @Test
    fun `works after transformation`() {
        val items = listOf(42, 43)
        val actual = items.consume(
            filterOn { a: Int -> a > 0 }.asList())
        assertEquals(listOf(items), actual)
    }
}
