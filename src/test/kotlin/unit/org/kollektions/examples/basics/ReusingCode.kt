package org.kollektions.examples.basics

import org.junit.jupiter.api.assertAll
import org.kollektions.consumers.*
import org.kollektions.transformations.filterOn
import kotlin.test.Test
import kotlin.test.assertEquals

class ReusingCode {

    private val things = listOf(
        Thing("Blue", "Circle"),
        Thing("Red", "Square")
    )

    @Test
    fun `counts read squares`() {
        val sut = getCountOfRedSquares()
        things.consumeByOne(sut)
        assertEquals(1L, sut.results())
    }

    @Test
    fun `use unit tested consumer with other consumers`() {
        val actual = things.consume(getCountOfRedSquares(), getBlueThings())
        println(actual)

        assertEquals(listOf(1L, things.subList(0, 1)), actual)
    }

    @Test
    fun `use unit tested consumer without sequences`() {
        val listener = ThingMessageListener()
        listener.onMessage(Thing("Blue", "Circle"))
        listener.onMessage(Thing("Red", "Square"))
        listener.onShutdown()
        assertEquals(1L, listener.results)
    }

    private class ThingMessageListener {
        var results: Long = 0L

        private val consumer = getCountOfRedSquares()

        fun onMessage(thing: Thing) {
            consumer.process(thing)
        }

        fun onShutdown() {
            consumer.stop()
            results = consumer.results() as Long
        }
    }
}

private data class Thing(val color: String, val shape: String)

private fun getCountOfRedSquares() =
    filterOn<Thing> { it.color == "Red" && it.shape == "Square" }.count()

private fun getBlueThings() =
    filterOn<Thing> { it.color == "Blue" }.asList()
