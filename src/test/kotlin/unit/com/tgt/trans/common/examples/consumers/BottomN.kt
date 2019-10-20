package com.tgt.trans.common.examples.consumers

import com.tgt.trans.common.konsumers.consumers.consume
import com.tgt.trans.common.konsumers.consumers.bottomBy
import com.tgt.trans.common.konsumers.consumers.bottomNBy
import kotlin.test.assertEquals
import kotlin.test.Test

class BottomNConsumerTest {

    @Test
    fun `compare by a projection`() {
        val things = listOf(ball, puck, ski, pole, boot)
        val projection = { a: Thing -> a.quantity }
        val actual = things.consume(bottomBy(projection), bottomNBy(2, projection))
        val expected2 = listOf(listOf(ball, puck), listOf(ski, boot))
        assertEquals(listOf(ball, puck), actual[0])
        assertEquals(expected2, actual[1])
    }

    @Test
    fun `provide a comparator`() {
        val things = listOf(ball, puck, ski, pole, boot)
        val comparator = { a: Thing, b: Thing -> a.quantity.compareTo(b.quantity) }
        val actual = things.consume(bottomBy(comparator), bottomNBy(2, comparator))
        val expected2 = listOf(listOf(ball, puck), listOf(ski, boot))
        assertEquals(listOf(ball, puck), actual[0])
        assertEquals(expected2, actual[1])
    }
    
    data class Thing(val name: String, val quantity: Int)

    private val ball = Thing("Ball", 1)
    private val puck = Thing("Puck", 1)
    private val ski = Thing("Ski", 2)
    private val pole = Thing("Pole", 3)
    private val boot = Thing("Boot", 2)
}

