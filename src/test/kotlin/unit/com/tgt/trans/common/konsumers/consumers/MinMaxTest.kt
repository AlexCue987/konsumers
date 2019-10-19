package com.tgt.trans.common.konsumers.consumers

import com.tgt.trans.common.konsumers.transformations.mapTo
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals

class MinMaxTest {
    private val items = listOf(1, 2, 3)

    @Test
    fun `handles empty`() {
        val actual = listOf<Int>().consume(min(), max())
        assertEquals(listOf(Optional.empty<Int>(), Optional.empty<Int>()), actual)
    }

    @Test
    fun `handles non empty`() {
        val actual = items.consume(min(), max())
        assertEquals(listOf(Optional.of(1), Optional.of(3)), actual)
    }

    @Test
    fun `works after transformation`() {
        val actual = items.consume(
            mapTo<Int, Int> { it+2 }.min(),
            mapTo<Int, Int> { it+2 }.max())
        assertEquals(listOf(Optional.of(3), Optional.of(5)), actual)
    }
}
