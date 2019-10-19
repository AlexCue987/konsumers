package com.tgt.trans.common.konsumers.transformations

import com.tgt.trans.common.konsumers.consumers.asList
import com.tgt.trans.common.konsumers.consumers.consume
import com.tgt.trans.common.konsumers.dispatchers.allOf
import com.tgt.trans.common.testutils.FakeStopTester
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class StateKeeperTest {
    private val stateToKeep = asList<Int>()
    private val sut = keepState(stateToKeep).asList()

    @Test
    fun `handles no items`() {
        assertTrue { stateToKeep.results().isEmpty() }
    }

    @Test
    fun `handles one item`() {
        val value = 42
        sut.process(value)
        assertEquals (listOf(value), stateToKeep.results())
    }

    @Test
    fun `handles several items`() {
        val items = listOf(42, 43)
        items.consume(sut)
        assertEquals (items, stateToKeep.results())
    }

    @Test
    fun `passes stop call downstream`() {
        val fakeStopTester = FakeStopTester<Int>()
        val sut = keepState(stateToKeep).allOf(fakeStopTester)
        sut.stop()

        assertTrue(fakeStopTester.isStopped())
    }
}
