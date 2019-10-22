package org.kollektions.transformations

import org.kollektions.consumers.Consumer
import org.kollektions.consumers.asList
import org.kollektions.consumers.consume
import org.kollektions.consumers.count
import io.mockk.*
import kotlin.test.*

class StopperTest {
    @Test
    fun handlesEmpty() {
        val actual = listOf<Int>().consume(stopWhen<Int> { it > 0 }.count())
        assertEquals(0L, actual[0])
    }

    @Test
    fun chainedHandlesEmpty() {
        val actual = listOf<Int>().consume(filterOn { a: Int -> a > 0 }.stopWhen { it > 1 }.count())
        assertEquals(0L, actual[0])
    }

    @Test
    fun `handles item that meets condition`() {
        val items = listOf(0, 1, 2)
        val actual = items.consume(stopWhen<Int> { it == 1 }.asList(),
            stopAfter<Int> { it == 1 }.asList())
        assertEquals(listOf(
            listOf(0, 1),
            listOf(0)
        ), actual)
    }

    @Test
    fun `chained handles item that meets condition`() {
        val items = listOf(0, 1, 2, 1)
        val actual = items.consume(
            filterOn { a: Int -> a > 0 }.stopWhen { it == 2 }.asList(),
            filterOn { a: Int -> a > 0 }.stopAfter { it == 2 }.asList())
        assertEquals(listOf(
            listOf(1, 2),
            listOf(1)
        ), actual)
    }

    @Test
    fun passesStopCall() {
        val consumer = mockk<Consumer<Int>> {
            every { stop() } just Runs
        }
        val sut = stopWhen<Int> { it > 1 }.build(consumer)
        sut.stop()
        verify(exactly = 1) { consumer.stop() }
    }
}
