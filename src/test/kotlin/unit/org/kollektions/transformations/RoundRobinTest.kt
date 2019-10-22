//package com.tgt.trans.common.aggregator2.decorators
//
//import com.tgt.trans.common.aggregator2.consumers.*
//import io.mockk.*
//import java.math.BigDecimal
//import kotlin.test.*
//
//class RoundRobinTest {
//    @Test
//    fun `validates roundRobin`() {
//        assertFailsWith<IllegalArgumentException> { listOf<Int>().consume(roundRobin<Int>(1).count()) }
//    }
//
//    @Test
//    fun handlesEmpty() {
//        val actual = listOf<Int>().consume(roundRobin<Int>(2).count())
//        assertEquals(listOf(0L, 0L), actual[0])
//    }
//
//    @Test
//    fun chainedHandlesEmpty() {
//        val actual = listOf<Int>().consume(filterOn { a: Int -> a > 0 }.roundRobin(2).count())
//        assertEquals(listOf(0L, 0L), actual[0])
//    }
//
//    @Test
//    fun handlesSomeItems() {
//        val items = listOf(42, 43, 44)
//        val actual = items.consume(roundRobin<Int>(2).asList())
//        assertEquals(listOf(listOf(42, 44), listOf(43)), actual[0])
//    }
//
//    @Test
//    fun chainedHandlesSomeItems() {
//        val items = listOf(42, 43, 44)
//        val actual = items.consume(filterOn { a: Int -> a > 0 }.roundRobin(2).asList())
//        assertEquals(listOf(listOf(42, 44), listOf(43)), actual[0])
//    }
//
//    @Test
//    fun providesEmptyCopy() {
//        val originalConsumer = roundRobin<Int>(2).count()
//        listOf(42, 43).consume(originalConsumer)
//        assertFalse(originalConsumer.isEmpty(), "Guardian assumption: not empty")
//        val sut = originalConsumer.emptyCopy()
//        assertTrue(sut.isEmpty())
//    }
//
//    @Test
//    fun passesStopCall() {
//        val actual = listOf(42, 43).consume(roundRobin<Int>(2).batches(2).asList())
//        assertEquals(listOf(listOf(listOf(42)), listOf(listOf(43))), actual[0])
//    }
//}
