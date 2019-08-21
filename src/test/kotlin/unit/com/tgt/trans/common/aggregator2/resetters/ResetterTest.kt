package com.tgt.trans.common.aggregator2.resetters

import com.tgt.trans.common.aggregator2.conditions.VanillaCondition
import com.tgt.trans.common.aggregator2.consumers.asList
import com.tgt.trans.common.aggregator2.consumers.consume
import kotlin.test.Test
import kotlin.test.assertEquals


class ResetterTest {
    private val resetOnValue = 3
    private val intermediateResultsTransformer = {a: Any, _: Any -> a as List<Int>}
    private val finalConsumer = asList<List<Int>>()
    private val resetOnCondition = ResetterOnCondition(false, VanillaCondition { a: Int -> a == resetOnValue } )
    private val resetAfterCondition = ResetterOnCondition(true, VanillaCondition { a: Int -> a == resetOnValue })

    @Test
    fun `empty series`() {
        listOf<Int>().consume(asList<Int>().withResetting(resetOnCondition, intermediateResultsTransformer, finalConsumer))
        assertEquals(listOf(), finalConsumer.results())
    }

    @Test
    fun `not empty series without reset`() {
        val listWithoutReset = listOf(1, 2)
        listWithoutReset.consume(asList<Int>().withResetting(resetOnCondition, intermediateResultsTransformer, finalConsumer))
        assertEquals(listOf(listWithoutReset), finalConsumer.results())
    }

    @Test
    fun `not empty series ending on reset`() {
        val listWithResetAtEnd = listOf(1, 2, resetOnValue)
        listWithResetAtEnd.consume(asList<Int>().withResetting(resetOnCondition, intermediateResultsTransformer, finalConsumer))
        assertEquals(listOf(listWithResetAtEnd.take(2), listWithResetAtEnd.takeLast(1)), finalConsumer.results())
    }

    @Test
    fun `not empty series ending after reset`() {
        val listWithResetAtEnd = listOf(1, 2, resetOnValue)
        listWithResetAtEnd.consume(asList<Int>().withResetting(resetAfterCondition, intermediateResultsTransformer, finalConsumer))
        assertEquals(listOf(listWithResetAtEnd), finalConsumer.results())
    }

    @Test
    fun `not empty series with reset in the middle, ending on reset`() {
        val listWithResetAtEnd = listOf(1, 2, resetOnValue, 4, 5)
        listWithResetAtEnd.consume(asList<Int>().withResetting(resetOnCondition, intermediateResultsTransformer, finalConsumer))
        assertEquals(listOf(listWithResetAtEnd.take(2), listWithResetAtEnd.takeLast(3)), finalConsumer.results())
    }

    @Test
    fun `not empty series with reset in the middle, ending after reset`() {
        val listWithResetAtEnd = listOf(1, 2, resetOnValue, 4, 5)
        listWithResetAtEnd.consume(asList<Int>().withResetting(resetAfterCondition, intermediateResultsTransformer, finalConsumer))
        assertEquals(listOf(listWithResetAtEnd.take(3), listWithResetAtEnd.takeLast(2)), finalConsumer.results())
    }
}

