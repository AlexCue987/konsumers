package com.tgt.trans.common.konsumers.transformations

import com.tgt.trans.common.konsumers.consumers.asList
import com.tgt.trans.common.konsumers.consumers.consume
import com.tgt.trans.common.konsumers.consumers.count
import com.tgt.trans.common.konsumers.dispatchers.allOf
import com.tgt.trans.common.testutils.FakeStopTester
import org.junit.jupiter.api.assertAll
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class MultipleStatesKeeperTest {
    private val listToKeep = asList<Int>()
    private val countToKeep = count<Int>()
    private val sut = keepStates(listToKeep, countToKeep).asList()

    @Test
    fun `handles no items`() {
        assertAll(
            { assertTrue { listToKeep.results().isEmpty() } },
            { assertEquals(0L, countToKeep.results()) }
        )
    }

    @Test
    fun `handles one item`() {
        val value = 42
        sut.process(value)

        assertAll(
            { assertEquals (listOf(value), listToKeep.results()) },
            { assertEquals(1L, countToKeep.results()) }
        )
    }

    @Test
    fun `handles several items`() {
        val items = listOf(42, 43)
        items.consume(sut)

        assertAll(
            { assertEquals (items, listToKeep.results()) },
            { assertEquals(2L, countToKeep.results()) }
        )
    }

    @Test
    fun `passes stop call downstream`() {
        val fakeStopTester = FakeStopTester<Int>()
        val sut = keepStates(listToKeep, countToKeep).allOf(fakeStopTester)
        sut.stop()

        assertTrue(fakeStopTester.isStopped())
    }
}
