package org.kollektions.transformations

import org.kollektions.consumers.asList
import org.kollektions.consumers.consume
import org.kollektions.consumers.count
import org.junit.jupiter.api.assertAll
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ChainedMultipleStatesKeeperTest {
    private val listToKeep = asList<Int>()
    private val countToKeep = count<Int>()
    private val sut = filterOn<Int> { it > 0 }.keepStates(listToKeep, countToKeep).asList()

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
}
