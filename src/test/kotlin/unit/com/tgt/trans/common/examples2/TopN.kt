package com.tgt.trans.common.examples2

import com.tgt.trans.common.aggregator2.consumers.consume
import com.tgt.trans.common.aggregator2.consumers.topNBy
import com.tgt.trans.common.aggregator2.decorators.filterOn
import org.junit.jupiter.api.assertAll
import kotlin.test.assertEquals
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

data class Thing(val name: String, val quantity: Int)

private val ball = Thing("Ball", 1)
private val puck = Thing("Puck", 1)
private val ski = Thing("Ski", 2)
private val pole = Thing("Pole", 3)
private val boot = Thing("Boot", 2)

class TopNConsumerTest {

    @Test
    fun `both buckets filled some items rejected`() {
        val actual = listOf(ball, puck, ski, pole, boot)
            .consume(
                topNBy(2) { a: Thing, b:Thing -> a.quantity.compareTo(b.quantity)},
                topNBy(2) { a: Thing -> a.quantity }
            )
        val expected = listOf(listOf(pole), listOf(ski, boot))
        assertAll(
            { assertEquals(expected, actual[0], "lambda") },
            { assertEquals(expected, actual[1], "projection") }
        )
    }
}

