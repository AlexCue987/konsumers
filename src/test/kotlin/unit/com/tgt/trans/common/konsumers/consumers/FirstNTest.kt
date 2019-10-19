package com.tgt.trans.common.konsumers.consumers

import com.tgt.trans.common.konsumers.transformations.filterOn
import kotlin.test.assertEquals
import kotlin.test.Test
import kotlin.test.assertTrue

class FirstNTest {
    val count = 2
    private val sut = FirstN<Int>(count)

    @Test
    fun `handles no items`() {
        val actual = listOf<Int>().consume(sut)
        assertTrue { sut.results().isEmpty() }
    }

    @Test
    fun `handles one item`() {
        val actual = listOf(42).consume(sut)
        assertEquals(listOf(42), actual[0])
    }

    @Test
    fun `handles exactly buffer size`() {
        val items = listOf(42, 43)
        val actual = items.consume(sut)
        assertEquals(items, actual[0])
    }

    @Test
    fun `handles more than buffer size`() {
        val items = listOf(42, 43, 44)
        val actual = items.consume(sut)
        assertEquals(items.take(count), actual[0])
    }

    @Test
    fun `works after transformation`() {
        val items = listOf(42, 43, 44)
        val actual = items.consume(filterOn<Int> { it > 0 }.firstN(2))
        assertEquals(items.take(count), actual[0])
    }
}
