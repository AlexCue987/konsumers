package com.tgt.trans.common.aggregator2.decorators

import com.tgt.trans.common.aggregator2.consumers.*
import io.mockk.*
import kotlin.test.*

class FirstTest {
    @Test
    fun `validates count`() {
        assertFailsWith<IllegalArgumentException> { listOf<Int>().consume(first<Int>(0).count()) }
    }

    @Test
    fun handlesEmpty() {
        val actual = listOf<Int>().consume(first<Int>(2).count())
        assertEquals(0L, actual[0])
    }

    @Test
    fun chainedHandlesEmpty() {
        val actual = listOf<Int>().consume(filterOn { a: Int -> a > 0 }.first(2).count())
        assertEquals(0L, actual[0])
    }

    @Test
    fun handlesOneItem() {
        val items = listOf(42)
        val actual = items.consume(first<Int>(2).asList())
        assertEquals(items, actual[0])
    }

    @Test
    fun chainedHandlesOneItem() {
        val items = listOf(42)
        val actual = items.consume(filterOn { a: Int -> a > 0 }.first(2).asList())
        assertEquals(items, actual[0])
    }

    @Test
    fun handlesExactlyCount() {
        val items = listOf(42, 43)
        val actual = items.consume(first<Int>(2).asList())
        assertEquals(items, actual[0])
    }

    @Test
    fun chainedHandlesExactlyCount() {
        val items = listOf(42, 43)
        val actual = items.consume(filterOn { a: Int -> a > 0 }.first(2).asList())
        assertEquals(items, actual[0])
    }

    @Test
    fun handlesMoreThanCount() {
        val items = listOf(42, 43, 44)
        val actual = items.consume(first<Int>(2).asList())
        assertEquals(items.take(2), actual[0])
    }

    @Test
    fun chainedHandlesMoreThanCount() {
        val items = listOf(42, 43, 44)
        val actual = items.consume(filterOn { a: Int -> a > 0 }.first(2).asList())
        assertEquals(items.take(2), actual[0])
    }

    @Test
    fun providesEmptyCopy() {
        val originalConsumer = first<Int>(1).count()
        listOf(42).consume(originalConsumer)
        assertFalse(originalConsumer.isEmpty(), "Guardian assumption: not empty")
        val sut = originalConsumer.emptyCopy()
        assertTrue(sut.isEmpty())
    }

    @Test
    fun passesStopCall() {
        val consumer = mockk<Consumer<Int>> {
            every { stop() } just Runs
        }
        val sut = first<Int>(1).build(consumer)
        sut.stop()
        verify(exactly = 1) { consumer.stop() }
    }
}
