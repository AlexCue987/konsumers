package org.kollektions.examples.consumers

import org.kollektions.consumers.consume
import org.kollektions.consumers.topBy
import org.kollektions.consumers.topNBy
import kotlin.test.assertEquals
import kotlin.test.Test

data class Thing(val name: String, val quantity: Int)

private val ball = Thing("Ball", 1)
private val puck = Thing("Puck", 1)
private val ski = Thing("Ski", 2)
private val pole = Thing("Pole", 3)
private val boot = Thing("Boot", 2)

class TopNConsumerTest {

    @Test
    fun `compare by a projection`() {
        val things = listOf(ball, puck, ski, pole, boot)
        val projection = { a: Thing -> a.quantity }
        val actual = things.consume(topBy(projection), topNBy(2, projection))
        val expected2 = listOf(listOf(pole), listOf(ski, boot))
        assertEquals(listOf(pole), actual[0])
        assertEquals(expected2, actual[1])
    }

    @Test
    fun `provide a comparator`() {
        val things = listOf(ball, puck, ski, pole, boot)
        val comparator = { a: Thing, b: Thing -> a.quantity.compareTo(b.quantity) }
        val actual = things.consume(topBy(comparator), topNBy(2, comparator))
        val expected2 = listOf(listOf(pole), listOf(ski, boot))
        assertEquals(listOf(pole), actual[0])
        assertEquals(expected2, actual[1])
    }
}

