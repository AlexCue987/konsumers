//package com.tgt.trans.common.aggregator2.resetters
//
//import com.tgt.trans.common.aggregator2.conditions.FirstItemEvaluation
//import com.tgt.trans.common.aggregator2.conditions.firstItemCondition
//import com.tgt.trans.common.aggregator2.conditions.lastItemCondition
//import com.tgt.trans.common.aggregator2.consumers.asList
//import com.tgt.trans.common.aggregator2.consumers.consume
//import com.tgt.trans.common.aggregator2.consumers.counter
//import com.tgt.trans.common.aggregator2.consumers.max
//import java.lang.Math.abs
//import java.util.*
//import kotlin.test.assertEquals
//import kotlin.test.Test
//
//class ResetterOnConditionTest {
//    private val positive = {a: Int -> a > 0}
//    private val increase = lastItemCondition( {previousValue: Int, currentValue: Int -> previousValue < currentValue},
//        firstItemEvaluation = FirstItemEvaluation.False)
//    private val intermediateResultsTransformerToLong = { a: Any, _: Any -> a as Long}
//    private val intermediateResultsTransformerToList = { a: Any, _: Any -> a as List<Int>}
//
//    private val bigChange = firstItemCondition {previousValue: Int, currentValue: Int -> abs(previousValue - currentValue) > 2}
//
//    @Test
//    fun `resetWhen handles empty series`() {
//        val actual = listOf<Int>().consume(max<Int>().resetWhen(positive, intermediateResultsTransformerToLong, asList()))
//        assertEquals(listOf<Optional<Int>>(), actual[0])
//    }
//
//    @Test
//    fun `resetAfter handles empty series`() {
//        val actual = listOf<Int>().consume(max<Int>().resetAfter(positive, intermediateResultsTransformerToLong, asList()))
//        assertEquals(listOf<Optional<Int>>(), actual[0])
//    }
//
//    @Test
//    fun `resetWhen handles exactly one event at start of series`() {
//        val actual = listOf(1, 0, -1).consume(counter<Int>().resetWhen(positive, intermediateResultsTransformerToLong, asList()))
//        assertEquals(listOf(3L), actual[0] )
//    }
//
//    @Test
//    fun `resetAfter handles exactly one event at start of series`() {
//        val actual = listOf(1, 0, 0).consume(counter<Int>().resetAfter(positive, intermediateResultsTransformerToLong, asList()))
//        assertEquals(listOf(1L, 2L), actual[0] )
//    }
//
//    @Test
//    fun `resetWhen handles exactly one event at end of series`() {
//        val actual = listOf(-1, 0, 1).consume(counter<Int>().resetWhen(positive, intermediateResultsTransformerToLong, asList()))
//        assertEquals(listOf(2L, 1L), actual[0] )
//    }
//
//    @Test
//    fun `resetWhen handles generic condition`() {
//        val actual = listOf(-1, -2, 1).consume(asList<Int>().resetWhen(increase, intermediateResultsTransformerToList, asList()))
//        assertEquals(listOf(listOf(-1, -2), listOf(1)), actual[0] )
//    }
//
//    @Test
//    fun `resetAfter handles exactly one event at end of series`() {
//        val actual = listOf(-1, 0, 1).consume(counter<Int>().resetAfter(positive, intermediateResultsTransformerToLong, asList()))
//        assertEquals(listOf(3L), actual[0] )
//    }
//
//    @Test
//    fun `resetWhen handles exactly one event in the middle of series`() {
//        val actual = listOf(-1, 0, 1, 0).consume(counter<Int>().resetWhen(positive, intermediateResultsTransformerToLong, asList()))
//        assertEquals(listOf(2L, 2L), actual[0] )
//    }
//
//    @Test
//    fun `resetAfter handles exactly one event in the middle of series`() {
//        val actual = listOf(-1, 0, 1, 0).consume(counter<Int>().resetAfter(positive, intermediateResultsTransformerToLong, asList()))
//        assertEquals(listOf(3L, 1L), actual[0] )
//    }
//
//    @Test
//    fun `resetAfter handles generic condition`() {
//        val actual = listOf(-1, -2, 1, 1).consume(asList<Int>().resetAfter(increase, intermediateResultsTransformerToList, asList()))
//        assertEquals(listOf(listOf(-1, -2, 1), listOf(1)), actual[0] )
//    }
//
//    @Test
//    fun `resetWhen handles two consecutive events`() {
//        val actual = listOf(1, 2, -1).consume(counter<Int>().resetWhen(positive, intermediateResultsTransformerToLong, asList()))
//        assertEquals(listOf(1L, 2L), actual[0] )
//    }
//
//    @Test
//    fun `resetAfter handles two consecutive events`() {
//        val actual = listOf(1, 2, 0).consume(counter<Int>().resetAfter(positive, intermediateResultsTransformerToLong, asList()))
//        assertEquals(listOf(1L, 1L, 1L), actual[0] )
//    }
//
//    @Test
//    fun `resets condition when series reset`() {
//        val actual = listOf(1, 2, 5, 6, 4).consume(asList<Int>().resetWhen(bigChange, intermediateResultsTransformerToList, asList()))
//        assertEquals(listOf(listOf(1, 2), listOf(5, 6, 4)), actual[0] )
//    }
//}
