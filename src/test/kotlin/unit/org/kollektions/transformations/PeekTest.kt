package org.kollektions.transformations

import org.kollektions.consumers.consume
import org.kollektions.consumers.count
import org.kollektions.dispatchers.allOf
import org.kollektions.testutils.FakeStopTester
import java.math.BigDecimal
import kotlin.test.assertEquals
import kotlin.test.Test
import kotlin.test.assertTrue

class PeekTest {
    @Test
    fun handlesEmpty() {
        var peekCounter = 0
        listOf<Int>().consume(peek<Int> { peekCounter += it }.count())
        assertEquals(0, peekCounter)
    }

    @Test
    fun handlesOneItem() {
        var peekCounter = 0
        val actual = listOf(42).consume(peek<Int> { peekCounter += it }.count())
        assertEquals(1L, actual[0])
        assertEquals(42, peekCounter)
    }

    @Test
    fun chainsAfterAnotherBuilder() {
        var peekCounter = 0
        val sut = mapTo { a: Int -> BigDecimal(a.toLong()).times(BigDecimal("2")) }
                .peek { peekCounter += it.toInt() }
                .count()
        val actual = listOf(1, 2, 3).consume(sut)
        assertEquals(3L, actual[0])
        assertEquals(12, peekCounter)
    }

    @Test
    fun passesStopCall() {
        var peekCounter = 0
        val stopTester = FakeStopTester<Int>()
        val sut = peek<Int> { peekCounter += it }.allOf(stopTester)
        sut.stop()
        assertTrue(stopTester.isStopped())
    }
}
