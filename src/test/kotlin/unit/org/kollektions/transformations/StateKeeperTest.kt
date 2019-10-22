package org.kollektions.transformations

import org.kollektions.consumers.asList
import org.kollektions.consumers.consume
import org.kollektions.dispatchers.allOf
import org.kollektions.testutils.FakeStopTester
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
