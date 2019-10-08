package com.tgt.trans.common.konsumers.consumers

import com.tgt.trans.common.konsumers.transformations.filterOn
import org.junit.jupiter.api.assertAll
import kotlin.test.assertEquals
import kotlin.test.Test

class BottomNConsumerTest {
    private data class Thing(val name: String, val quantity: Int)

    private val ball = Thing("Ball", 1)
    private val puck = Thing("Puck", 1)
    private val ski = Thing("Ski", 2)
    private val pole = Thing("Pole", 3)
    private val boot = Thing("Boot", 2)

    private val sutLambda = bottomNBy(2) { a: Thing, b:Thing -> a.quantity.compareTo(b.quantity)}

    private val sutProjection = bottomNBy(2) { a: Thing -> a.quantity }

    private val sutChainedLambda = filterOn<Thing> { it.quantity > 0 }
        .bottomNBy(2) { a: Thing, b:Thing -> a.quantity.compareTo(b.quantity)}

    private val sutChainedProjection = filterOn<Thing> { it.quantity > 0 }
        .bottomNBy(2) { a: Thing -> a.quantity }

    @Test
    fun `handles empty`() {
        val actual = listOf<Thing>().consume(sutLambda, sutProjection, sutChainedLambda, sutChainedProjection)
        assertAll(
            { assertEquals(listOf<List<Thing>>(), actual[0], "lambda") },
            { assertEquals(listOf<List<Thing>>(), actual[1], "projection") },
            { assertEquals(listOf<List<Thing>>(), actual[2], "chained lambda") },
            { assertEquals(listOf<List<Thing>>(), actual[3], "chained projection") }
        )
    }

    @Test
    fun `handles one item`() {
        val actual = listOf(ball).consume(sutLambda, sutProjection, sutChainedLambda, sutChainedProjection)
        val expected = listOf(ball)
        assertAll(
            { assertEquals(listOf(expected), actual[0], "lambda") },
            { assertEquals(listOf(expected), actual[1], "projection") },
            { assertEquals(listOf(expected), actual[2], "chained lambda") },
            { assertEquals(listOf(expected), actual[3], "chained projection") }
        )
    }

    @Test
    fun `two items in a tie go to one bucket`() {
        val actual = listOf(ball, puck).consume(sutLambda, sutProjection, sutChainedLambda, sutChainedProjection)
        val expected = listOf(ball, puck)
        assertAll(
            { assertEquals(listOf(expected), actual[0], "lambda") },
            { assertEquals(listOf(expected), actual[1], "projection") },
            { assertEquals(listOf(expected), actual[2], "chained lambda") },
            { assertEquals(listOf(expected), actual[3], "chained projection") }
        )
    }

    @Test
    fun `both buckets filled`() {
        val actual = listOf(ball, puck, ski).consume(sutLambda, sutProjection, sutChainedLambda, sutChainedProjection)
        val expected = listOf(listOf(ball, puck), listOf(ski))
        assertAll(
            { assertEquals(expected, actual[0], "lambda") },
            { assertEquals(expected, actual[1], "projection") },
            { assertEquals(expected, actual[2], "chained lambda") },
            { assertEquals(expected, actual[3], "chained projection") }
        )
    }

    @Test
    fun `both buckets filled some items rejected`() {
        val actual = listOf(ball, puck, ski, pole, boot).consume(sutLambda, sutProjection, sutChainedLambda, sutChainedProjection)
        val expected = listOf(listOf(ball, puck), listOf(ski, boot))
        assertAll(
            { assertEquals(expected, actual[0], "lambda") },
            { assertEquals(expected, actual[1], "projection") },
            { assertEquals(expected, actual[2], "chained lambda") },
            { assertEquals(expected, actual[3], "chained projection") }
        )
    }
}

