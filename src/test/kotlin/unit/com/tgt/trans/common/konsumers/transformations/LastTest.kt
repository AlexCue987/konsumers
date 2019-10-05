package com.tgt.trans.common.konsumers.transformations

import com.tgt.trans.common.konsumers.consumers.*
import io.mockk.*
import kotlin.test.*

class LastTest {
    @Test
    fun `validates count`() {
        assertFailsWith<IllegalArgumentException> { listOf<Int>().consume(last<Int>(0).count()) }
    }

    @Test
    fun handlesEmpty() {
        val actual = listOf<Int>().consume(last<Int>(2).count())
        assertEquals(0L, actual[0])
    }

    @Test
    fun chainedHandlesEmpty() {
        val actual = listOf<Int>().consume(filterOn { a: Int -> a > 0 }.last(2).count())
        assertEquals(0L, actual[0])
    }

    @Test
    fun handlesOneItem() {
        val items = listOf(42)
        val actual = items.consume(last<Int>(2).asList())
        assertEquals(items, actual[0])
    }

    @Test
    fun chainedHandlesOneItem() {
        val items = listOf(42)
        val actual = items.consume(filterOn { a: Int -> a > 0 }.last(2).asList())
        assertEquals(items, actual[0])
    }

    @Test
    fun handlesExactlyCount() {
        val items = listOf(42, 43)
        val actual = items.consume(last<Int>(2).asList())
        assertEquals(items, actual[0])
    }

    @Test
    fun chainedHandlesExactlyCount() {
        val items = listOf(42, 43)
        val actual = items.consume(filterOn { a: Int -> a > 0 }.last(2).asList())
        assertEquals(items, actual[0])
    }

    @Test
    fun handlesMoreThanCount() {
        val items = listOf(42, 43, 44)
        val actual = items.consume(last<Int>(2).asList())
        assertEquals(listOf(43, 44), actual[0])
    }

    @Test
    fun chainedHandlesMoreThanCount() {
        val items = listOf(42, 43, 44)
        val actual = items.consume(filterOn { a: Int -> a > 0 }.last(2).asList())
        assertEquals(listOf(43, 44), actual[0])
    }

    @Test
    fun passesStopCall() {
        val consumer = mockk<Consumer<Int>> {
            every { stop() } just Runs
        }
        val sut = last<Int>(1).build(consumer)
        sut.stop()
        verify(exactly = 1) { consumer.stop() }
    }
}
