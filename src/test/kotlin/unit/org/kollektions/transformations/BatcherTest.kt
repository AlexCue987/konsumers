package org.kollektions.transformations

import org.kollektions.consumers.asList
import org.kollektions.consumers.consume
import org.kollektions.dispatchers.allOf
import org.kollektions.testutils.FakeStopTester
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class BatcherTest {
    @Test
    fun handlesEmpty() {
        val actual = listOf<Int>().consume(
            batches<Int>(2).asList(),
            filterOn { a: Int -> a > 0 }.batches(2).asList())
        assertEquals(listOf(emptyList<List<Int>>(), emptyList()), actual)
    }

    @Test
    fun handlesOneItem() {
        val answer = 42
        val actual = listOf(answer).consume(batches<Int>(2).asList(),
            filterOn { a: Int -> a > 0 }.batches(2).asList())
        val expectedBatch = listOf(answer)
        assertEquals(listOf(listOf(expectedBatch), listOf(expectedBatch)), actual)
    }

    @Test
    fun handlesOneCompleteBatch() {
        val items = listOf(42, 43)
        val batchSize = 2
        assertEquals(batchSize, items.size, "Guardian assumption: exactly one batch")
        val actual = items.consume(batches<Int>(batchSize).asList(),
            filterOn { a: Int -> a > 0 }.batches(batchSize).asList())
        assertEquals(listOf(listOf(items), listOf(items)), actual)
    }

    @Test
    fun handlesOneCompleteAndOnePartialBatch() {
        val items = listOf(42, 43, 44)
        val batchSize = 2
        val batch1 = items.take(2)
        val batch2 = items.takeLast(1)
        assertTrue("Guardian assumption: more than one batch and less than two")
            { batchSize < items.size && items.size < batchSize * 2 }
        val actual = items.consume(batches<Int>(batchSize).asList(),
            filterOn { a: Int -> a > 0 }.batches(batchSize).asList())
        assertEquals(listOf(listOf(batch1, batch2), listOf(batch1, batch2)), actual)
    }

    @Test
    fun handlesTwoCompleteBatches() {
        val items = listOf(42, 43, 44, 45)
        val batch1 = items.take(2)
        val batch2 = items.takeLast(2)
        val batchSize = 2
        assertEquals(batchSize * 2, items.size, "Guardian assumption: exactly two batches")
        val actual = items.consume(batches<Int>(batchSize).asList(),
            filterOn { a: Int -> a > 0 }.batches(batchSize).asList())
        assertEquals(listOf(listOf(batch1, batch2), listOf(batch1, batch2)), actual)
    }

    @Test
    fun `passes stop downstream`() {
        val fakeStopTester = FakeStopTester<List<Int>>()
        val sut = batches<Int>(2).allOf<Int, List<Int>>(fakeStopTester)
        sut.stop()
        assertTrue(fakeStopTester.isStopped())
    }
}

