package com.tgt.trans.common.aggregator2.decorators

import com.tgt.trans.common.aggregator2.consumers.Consumer
import com.tgt.trans.common.aggregator2.consumers.asList
import com.tgt.trans.common.aggregator2.consumers.consume
import com.tgt.trans.common.aggregator2.consumers.count
import io.mockk.*
import kotlin.test.*

class StarterTest {
    @Test
    fun handlesEmpty() {
        val actual = listOf<Int>().consume(startWhen<Int> { it > 0 }.count())
        assertEquals(0L, actual[0])
    }

    @Test
    fun chainedHandlesEmpty() {
        val actual = listOf<Int>().consume(filterOn { a: Int -> a > 0 }.startWhen { it > 1 }.count())
        assertEquals(0L, actual[0])
    }

    @Test
    fun `handles item that meets condition`() {
        val items = listOf(0, 1, 2)
        val actual = items.consume(startWhen<Int> { it > 0 }.asList(),
            startAfter<Int> { it > 0 }.asList())
        assertEquals(listOf(
            listOf(1, 2),
            listOf(2)
        ), actual)
    }

    @Test
    fun `chained handles item that meets condition`() {
        val items = listOf(0, 1, 2, 1)
        val actual = items.consume(
            filterOn { a: Int -> a > 0 }.startWhen { it > 1 }.asList(),
            filterOn { a: Int -> a > 0 }.startAfter { it > 1 }.asList())
        assertEquals(listOf(
            listOf(2, 1),
            listOf(1)
        ), actual)
    }

    @Test
    fun providesEmptyCopy() {
        val originalConsumer = startWhen<Int> { it > 1 }.count()
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
        val sut = startWhen<Int> { it > 1 }.build(consumer)
        sut.stop()
        verify(exactly = 1) { consumer.stop() }
    }
}
