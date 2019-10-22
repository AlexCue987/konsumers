package org.kollektions.transformations

import org.kollektions.consumers.asList
import org.kollektions.consumers.consume
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ChainedStateKeeperTest {
    private val stateToKeep = asList<Int>()
    private val sut = filterOn<Int> { it > 0 }. keepState(stateToKeep).asList()

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
}
