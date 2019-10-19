package com.tgt.trans.common.konsumers.consumers

import com.tgt.trans.common.konsumers.transformations.filterOn
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class FirstTest {
    val count = 2
    private val sut = First<Int>()

    @Test
    fun `handles no items`() {
        val actual = listOf<Int>().consume(sut)
        assertFalse { sut.results().isPresent }
    }

    @Test
    fun `handles one item`() {
        val actual = listOf(42).consume(sut)
        assertEquals(Optional.of(42), actual[0])
    }

    @Test
    fun `handles more than one item`() {
        val items = listOf(42, 43)
        val actual = items.consume(sut)
        assertEquals(Optional.of(42), actual[0])
    }

    @Test
    fun `works after transformation`() {
        val items = listOf(42, 43, 44)
        val actual = items.consume(filterOn<Int> { it > 0 }.first())
        assertEquals(Optional.of(42), actual[0])
    }
}
