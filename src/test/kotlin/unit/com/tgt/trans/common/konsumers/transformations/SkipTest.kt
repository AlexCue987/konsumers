package com.tgt.trans.common.konsumers.transformations

import com.tgt.trans.common.konsumers.consumers.*
import io.mockk.*
import kotlin.test.*

class SkipTest {
    @Test
    fun `validates count`() {
        assertFailsWith<IllegalArgumentException> { listOf<Int>().consume(skip<Int>(0).count()) }
    }

    @Test
    fun handlesEmpty() {
        val actual = listOf<Int>().consume(skip<Int>(2).count())
        assertEquals(0L, actual[0])
    }

    @Test
    fun chainedHandlesEmpty() {
        val actual = listOf<Int>().consume(filterOn { a: Int -> a > 0 }.skip(2).count())
        assertEquals(0L, actual[0])
    }

    @Test
    fun handlesOneItem() {
        val items = listOf(42, 43, 44)
        val actual = items.consume(skip<Int>(2).asList())
        assertEquals(listOf(items[2]), actual[0])
    }

    @Test
    fun chainedHandlesOneItem() {
        val items = listOf(42, 43, 44)
        val actual = items.consume(filterOn { a: Int -> a > 0 }.skip(2).asList())
        assertEquals(listOf(items[2]), actual[0])
    }

    @Test
    fun passesStopCall() {
        val consumer = mockk<Consumer<Int>> {
            every { stop() } just Runs
        }
        val sut = skip<Int>(1).build(consumer)
        sut.stop()
        verify(exactly = 1) { consumer.stop() }
    }
}
