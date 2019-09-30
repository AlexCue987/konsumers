package com.tgt.trans.common.aggregator2.decorators

import com.tgt.trans.common.aggregator2.consumers.Consumer
import com.tgt.trans.common.aggregator2.consumers.asList
import com.tgt.trans.common.aggregator2.consumers.consume
import com.tgt.trans.common.aggregator2.consumers.count
import io.mockk.*
import kotlin.test.*

class WithPrecedingTest {
    @Test
    fun handlesEmpty() {
        val actual = listOf<Int>().consume(withPreceding<Int>(1).count())
        assertEquals(0L, actual[0])
    }

    @Test
    fun chainedHandlesEmpty() {
        val actual = listOf<Int>().consume(filterOn { a: Int -> a > 0 }.withPreceding(1).count())
        assertEquals(0L, actual[0])
    }

    @Test
    fun `handles incomplete series`() {
        val items = listOf(0)
        val actual = items.consume(
            toPairs<Int>().asList(),
            toRollingSeries<Int>().asList(),
            toRollingIncompleteSeries<Int>().asList())
        assertEquals(listOf(listOf<Pair<Int, Int>>(),
            listOf<List<Int>>(),
            listOf(listOf(0))
        ), actual)
    }

    @Test
    fun `handles one complete series`() {
        val items = listOf(0, 1)
        val actual = items.consume(
            toPairs<Int>().asList(),
            toRollingSeries<Int>().asList(),
            toRollingIncompleteSeries<Int>().asList())
        assertEquals(listOf(listOf(Pair(0, 1)),
            listOf(listOf(0, 1)),
            listOf(listOf(0), listOf(0, 1))
        ), actual)
    }

    @Test
    fun `handles two complete series`() {
        val items = listOf(0, 1, 2)
        val actual = items.consume(
            toPairs<Int>().asList(),
            toRollingSeries<Int>().asList(),
            toRollingIncompleteSeries<Int>().asList())
        assertEquals(listOf(listOf(Pair(0, 1), Pair(1, 2)),
            listOf(listOf(0, 1), listOf(1, 2)),
            listOf(listOf(0), listOf(0, 1), listOf(1, 2))
        ), actual)
    }

    @Test
    fun `chained handles incomplete series`() {
        val items = listOf(0)
        val actual = items.consume(
            filterOn { a: Int -> a >= 0 }.toPairs().asList(),
            filterOn { a: Int -> a >= 0 }.toRollingSeries().asList(),
            filterOn { a: Int -> a >= 0 }.toRollingIncompleteSeries().asList())
        assertEquals(listOf(listOf<Pair<Int, Int>>(),
            listOf<List<Int>>(),
            listOf(listOf(0))
        ), actual)
    }

    @Test
    fun `chained handles one complete series`() {
        val items = listOf(0, 1)
        val actual = items.consume(
            filterOn { a: Int -> a >= 0 }.toPairs().asList(),
            filterOn { a: Int -> a >= 0 }.toRollingSeries().asList(),
            filterOn { a: Int -> a >= 0 }.toRollingIncompleteSeries().asList())
        assertEquals(listOf(listOf(Pair(0, 1)),
            listOf(listOf(0, 1)),
            listOf(listOf(0), listOf(0, 1))
        ), actual)
    }

    @Test
    fun `chained handles two complete series`() {
        val items = listOf(0, 1, 2)
        val actual = items.consume(
            filterOn { a: Int -> a >= 0 }.toPairs().asList(),
            filterOn { a: Int -> a >= 0 }.toRollingSeries().asList(),
            filterOn { a: Int -> a >= 0 }.toRollingIncompleteSeries().asList())
        assertEquals(listOf(listOf(Pair(0, 1), Pair(1, 2)),
            listOf(listOf(0, 1), listOf(1, 2)),
            listOf(listOf(0), listOf(0, 1), listOf(1, 2))
        ), actual)
    }

    @Test
    fun passesStopCall() {
        val consumer = mockk<Consumer<IWithPreceding<Int>>> {
            every { stop() } just Runs
        }
        val sut = WithPrecedingBuilder<Int>(1, true).build(consumer)
        sut.stop()
        verify(exactly = 1) { consumer.stop() }
    }
}
