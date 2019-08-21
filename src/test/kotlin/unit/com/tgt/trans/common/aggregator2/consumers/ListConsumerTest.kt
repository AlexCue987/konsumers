package com.tgt.trans.common.aggregator2.consumers

import com.tgt.trans.common.aggregator2.decorators.filterOn
import kotlin.test.assertEquals
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

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
    fun providesEmptyCopy() {
        val originalConsumer = asList<Int>()
        listOf(42).consume(originalConsumer)
        assertFalse("Guardian assumption: not empty") { originalConsumer.isEmpty() }
        val sut = originalConsumer.emptyCopy()
        assertTrue { sut.isEmpty() }
    }
}
