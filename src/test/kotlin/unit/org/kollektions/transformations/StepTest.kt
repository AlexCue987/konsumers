package org.kollektions.transformations

import io.mockk.*
import org.kollektions.consumers.Consumer
import org.kollektions.consumers.asList
import org.kollektions.consumers.consume
import org.kollektions.consumers.count
import kotlin.test.*

class StepTest {
    @Test
    fun `validates step`() {
        assertFailsWith<IllegalArgumentException> { listOf<Int>().consume(step<Int>(1).count()) }
    }

    @Test
    fun handlesEmpty() {
        val actual = listOf<Int>().consume(step<Int>(2).count())
        assertEquals(0L, actual[0])
    }

    @Test
    fun chainedHandlesEmpty() {
        val actual = listOf<Int>().consume(filterOn { a: Int -> a > 0 }.step(2).count())
        assertEquals(0L, actual[0])
    }

    @Test
    fun handlesOneItem() {
        val items = listOf(42, 43, 44)
        val actual = items.consume(step<Int>(2).asList())
        assertEquals(listOf(items[1]), actual[0])
    }

    @Test
    fun chainedHandlesOneItem() {
        val items = listOf(42, 43, 44)
        val actual = items.consume(filterOn { a: Int -> a > 0 }.step(2).asList())
        assertEquals(listOf(items[1]), actual[0])
    }

    @Test
    fun handlesTwoItems() {
        val items = listOf(42, 43, 44, 45)
        val actual = items.consume(step<Int>(2).asList())
        assertEquals(listOf(items[1], items[3]), actual[0])
    }

    @Test
    fun chainedHandlesTwoItems() {
        val items = listOf(42, 43, 44, 45)
        val actual = items.consume(filterOn { a: Int -> a > 0 }.step(2).asList())
        assertEquals(listOf(items[1], items[3]), actual[0])
    }

    @Test
    fun passesStopCall() {
        val consumer = mockk<Consumer<Int>> {
            every { stop() } just Runs
        }
        val sut = step<Int>(2).build(consumer)
        sut.stop()
        verify(exactly = 1) { consumer.stop() }
    }
}
