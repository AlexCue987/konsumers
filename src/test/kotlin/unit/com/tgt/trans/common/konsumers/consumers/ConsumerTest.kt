package com.tgt.trans.common.konsumers.consumers

import com.tgt.trans.common.konsumers.transformations.filterOn
import com.tgt.trans.common.konsumers.transformations.mapTo
import com.tgt.trans.common.testutils.FakeStopTester
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ConsumerTest {
    @Test
    fun worksWithSequence() {
        val actual = (1..3).asSequence().consume(max())
        assertEquals(listOf(Optional.of(3)), actual)
    }

    @Test
    fun `works with Sequence and one Consumer`() {
        val actual = (1..3).asSequence().consumeByOne(max())
        assertEquals(Optional.of(3), actual)
    }

    @Test
    fun `works with Sequence and resultsMapper`() {
        val actual = (1..3).asSequence().consume(
            {consumersList: List<Consumer<Int>> -> resultsAsString(consumersList) },
            max())
        assertEquals("Optional[3]", actual)
    }

    fun resultsAsString(consumersList: List<Consumer<Int>>) = consumersList[0].results().toString()

    @Test
    fun worksWithIterable() {
        val actual = (1..3).asIterable().consume(max())
        assertEquals(listOf(Optional.of(3)), actual)
    }

    @Test
    fun `works with Iterable and one Consumer`() {
        val actual = (1..3).asIterable().consumeByOne(max())
        assertEquals(Optional.of(3), actual)
    }

    @Test
    fun `works with Iterable and resultsMapper`() {
        val actual = (1..3).asIterable().consume(
            {consumersList: List<Consumer<Int>> -> resultsAsString(consumersList) },
            max())
        assertEquals("Optional[3]", actual)
    }

    @Test
    fun `consume stops after consuming all`() {
        val consumer = FakeStopTester<Int>()
        val consumer2 = FakeStopTester<Int>()
        (1..3).asSequence().consume(consumer, consumer2)
        assertTrue(consumer.isStopped())
        assertTrue(consumer2.isStopped())
    }

    @Test
    fun `consume stops after consuming empty `() {
        val consumer = FakeStopTester<Int>()
        val consumer2 = FakeStopTester<Int>()
        listOf<Int>().asSequence().consume(consumer, consumer2)
        assertTrue(consumer.isStopped())
    }

    @Test
    fun `consumeByOne stops after consuming all`() {
        val consumer = FakeStopTester<Int>()
        (1..3).asSequence().consumeByOne(consumer)
        assertTrue(consumer.isStopped())
    }

    @Test
    fun `consumeByOne stops after consuming empty `() {
        val consumer = FakeStopTester<Int>()
        listOf<Int>().asSequence().consumeByOne(consumer)
        assertTrue(consumer.isStopped())
    }

    @Test
    fun mapTo() {
        val sut = com.tgt.trans.common.konsumers.transformations.mapTo { a: Int -> a + 1 }.count()
        sut.process(1)
        sut.process(2)
        assertEquals(2L, sut.results())
    }

    @Test
    fun ratio() {
        val sut = com.tgt.trans.common.konsumers.transformations.mapTo { a: Int -> a + 1 }.ratioOf { it%2 == 1 }
        (1..5).forEach { sut.process(it) }
        assertEquals(Ratio(2, 5), sut.results())
    }

    @Test
    fun filterOn_standalone() {
        val actual = listOf(1, 2, 3, 4, 5).consume(filterOn { it: Int -> it % 2 == 0 }.count())
        assertEquals(2L, actual[0])
    }

    @Test
    fun filterOn_chainedAfter() {
        val actual = listOf(1, 2, 3, 4, 5).consume(
                com.tgt.trans.common.konsumers.transformations.mapTo { a: Int -> a + 1 }.filterOn { it: Int -> it % 2 == 0 }.count(),
            count())
        assertEquals(listOf(3L, 5L), actual)
    }

    @Test
    fun filterOnMapTo() {
        val actual = listOf(1, 2, 3, 4, 5).consume(
                filterOn { it: Int -> it % 2 == 0 }.mapTo{ a: Int -> a + 1}.count(),
            count())
        assertEquals(listOf(2L, 5L), actual)
    }
}
